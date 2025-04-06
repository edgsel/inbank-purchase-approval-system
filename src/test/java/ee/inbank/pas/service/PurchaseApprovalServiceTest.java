package ee.inbank.pas.service;

import ee.inbank.model.CustomerPurchaseRequest;
import ee.inbank.pas.dto.PurchaseApprovalResult;
import ee.inbank.pas.dto.PurchaseStatus;
import ee.inbank.pas.exception.EntityNotFoundException;
import ee.inbank.pas.persistance.entity.Customer;
import ee.inbank.pas.persistance.entity.PurchasingProfile;
import ee.inbank.pas.persistance.repository.CustomerRepository;
import ee.inbank.pas.persistance.repository.PurchaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PurchaseApprovalServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private PurchaseApprovalCalculationService purchaseApprovalCalculationService;

    @InjectMocks
    private PurchaseApprovalService purchaseApprovalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("GIVEN a customer WHEN is not found THEN throws exception")
    void customerNotFoundThrowsException() {
        var request = buildRequest(123L, new BigDecimal("100.00"));

        when(customerRepository.findByPersonalId(request.getPersonalId())).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () ->
            purchaseApprovalService.getPurchaseApprovalResult(request)
        );

        verify(purchaseRepository, never()).save(any());
    }

    @Test
    @DisplayName("GIVEN a ineligible customer WHEN requests a purchase approval THEN gets rejected")
    void ineligibleCustomerRejected() {
        var request = buildRequest(123L, new BigDecimal("100.00"));
        var ineligibleCustomer = buildCustomer(request.getPersonalId(), Customer.CustomerStatus.INELIGIBLE);

        when(customerRepository.findByPersonalId(request.getPersonalId())).thenReturn(ineligibleCustomer);

        var result = purchaseApprovalService.getPurchaseApprovalResult(request);

        assertFalse(result.approved());
        assertEquals(PurchaseStatus.REJECTED, result.purchaseStatus());

        verify(purchaseRepository, times(1)).save(any());
        verify(purchaseApprovalCalculationService, never()).getApprovalResult(anyInt(), any(), anyInt());
    }

    @Test
    @DisplayName("GIVEN a eligible customer WHEN requests a purchase approval THEN proceeds to purchase approval calculation")
    void eligibleCustomerCallsPurchaseApprovalCalculationService() {
        var eligibleCustomer = buildCustomer(123L, Customer.CustomerStatus.ELIGIBLE);
        var request = buildRequest(eligibleCustomer.getPersonalId(), new BigDecimal("500.00"));
        var mockResult = buildResult();

        when(customerRepository.findByPersonalId(eligibleCustomer.getPersonalId())).thenReturn(eligibleCustomer);
        when(purchaseApprovalCalculationService.getApprovalResult(
            eligibleCustomer.getPurchasingProfile().getFinancialCapacityFactor(),
            request.getAmount(),
            request.getPaymentPeriodInMonths())
        ).thenReturn(mockResult);

        var result = purchaseApprovalService.getPurchaseApprovalResult(request);

        assertTrue(result.approved());
        assertEquals(PurchaseStatus.APPROVED, result.purchaseStatus());

        verify(purchaseApprovalCalculationService, times(1)).getApprovalResult(any(), any(), anyInt());
        verify(purchaseRepository, times(1)).save(any());
    }

    private static CustomerPurchaseRequest buildRequest(Long personalId, BigDecimal amount) {
        return CustomerPurchaseRequest.builder()
            .personalId(personalId)
            .amount(amount)
            .paymentPeriodInMonths(6)
            .build();
    }

    private static Customer buildCustomer(Long personalId, Customer.CustomerStatus status) {
        return Customer.builder()
            .id(2L)
            .personalId(personalId)
            .status(status)
            .purchasingProfile(PurchasingProfile.builder().financialCapacityFactor(1000).build())
            .build();
    }

    private static PurchaseApprovalResult buildResult() {
        return PurchaseApprovalResult.builder()
            .amount(new BigDecimal("400.00"))
            .paymentPeriodInMonths(6)
            .purchaseStatus(PurchaseStatus.APPROVED)
            .approved(true)
            .build();
    }
}
