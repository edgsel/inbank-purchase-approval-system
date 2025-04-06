package ee.inbank.pas.service;

import ee.inbank.pas.dto.PurchaseStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class PurchaseApprovalCalculationServiceTest {

    private final PurchaseApprovalCalculationService calculationService = new PurchaseApprovalCalculationService();
    private final PurchaseApprovalCalculationService spyCalculationService = spy(PurchaseApprovalCalculationService.class);

    @Test
    @DisplayName("GIVEN a customer with 500 factor WHEN purchase approval is requested THEN is approved immediately")
    void approvedImmediately() {
        var factor = 500;
        var amount = new BigDecimal("200.00");
        var periodInMonths = 6;

        var result = calculationService.getApprovalResult(factor, amount, periodInMonths);

        assertTrue(result.approved());
        assertEquals(PurchaseStatus.APPROVED, result.purchaseStatus());
        assertTrue(result.amount().compareTo(amount) >= 0);
    }

    @Test
    @DisplayName("GIVEN purchase request with 100 factor WHEN requested purchase amount is too big THEN service tries to max amount within the range")
    void rejectedThenFindInRange() {
        var factor = 100;
        var amount = new BigDecimal("1000.00");
        var periodInMonths = 6;

        var result = calculationService.getApprovalResult(factor, amount, periodInMonths);

        // May happen that 1000.00 gets rejected, but service will still try to find some amount to approve
        if (result.purchaseStatus() == PurchaseStatus.APPROVED_WITH_AMOUNT_RANGE) {
            assertTrue(result.approved());
            assertTrue(result.amount().compareTo(BigDecimal.ONE) > 0);
        } else if (result.purchaseStatus() == PurchaseStatus.REJECTED) {
            assertFalse(result.approved());
        }
    }

    @Test
    @DisplayName("GIVEN purchase request WHEN requested amount gets rejected THEN service tries to extend payment period to get the approval")
    void testExtendPaymentPeriod() {
        // This test is a really rare corner case, because in most situations the request is getting approved by 2nd scenario (maxApprovedAmountInRange)
        // For this test I am going to mock calculateApprovalScore() to return < 1.00 and findMaxApprovedAmount 0.00

        var factor = 50;
        var amount = new BigDecimal("300.00");
        var periodInMonths = 6;

        doReturn(0.90).when(spyCalculationService).calculateApprovalScore(factor, amount, periodInMonths);
        doReturn(BigDecimal.ZERO).when(spyCalculationService).findMaxApprovedAmount(factor, BigDecimal.ONE, amount, periodInMonths);

        var result = spyCalculationService.getApprovalResult(factor, amount, periodInMonths);

        assertEquals(PurchaseStatus.APPROVED_WITH_EXTENDED_PAYMENT_PERIOD, result.purchaseStatus());
        assertTrue(result.approved());
        assertTrue(result.paymentPeriodInMonths() > 6);
    }

    @Test
    @DisplayName("GIVEN purchase request WHEN all scenarios return rejection THEN return reject the request")
    void testCompletelyRejected() {
        // Mock all three steps of purchase approval
        var factor = 100;
        var amount = new BigDecimal("5000.00");
        var periodInMonths = 24;

        doReturn(0.90).when(spyCalculationService).calculateApprovalScore(factor, amount, periodInMonths);
        doReturn(BigDecimal.ZERO).when(spyCalculationService).findMaxApprovedAmount(factor, BigDecimal.ONE, amount, periodInMonths);
        doReturn(null).when(spyCalculationService).findMaxApprovedPeriod(factor, BigDecimal.ONE, periodInMonths);

        var result = spyCalculationService.getApprovalResult(factor, amount, periodInMonths);

        assertFalse(result.approved());
        assertEquals(PurchaseStatus.REJECTED, result.purchaseStatus());
    }
}
