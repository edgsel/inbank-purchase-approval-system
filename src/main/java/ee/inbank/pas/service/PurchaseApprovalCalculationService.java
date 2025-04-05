package ee.inbank.pas.service;

import ee.inbank.pas.dto.PurchaseApprovalResult;
import ee.inbank.pas.dto.PurchaseStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static ee.inbank.pas.util.DtoBuildersUtil.buildPurchaseApprovalResult;

@Service
@AllArgsConstructor
public class PurchaseApprovalCalculationService {

    private static final int ROUNDING_SCALE = 2;
    private static final int APPROVAL_THRESHOLD = 1;
    private static final int MAX_PURCHASE_PERIOD_IN_MONTHS = 24;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_DOWN;
    private static final BigDecimal MAX_PURCHASE_AMOUNT = new BigDecimal("5000.00");
    private static final BigDecimal MIN_AMOUNT_INCREMENT = new BigDecimal("0.01");
    private static final BigDecimal DIVISOR = new BigDecimal("2");

    public PurchaseApprovalResult getApprovalResult(Integer financialCapacityFactor, BigDecimal amount, Integer periodInMonths) {
        var approvalScore = calculateApprovalScore(financialCapacityFactor, amount, periodInMonths);

        // 1.
        if (isApproved(approvalScore)) {
            var maxAmount = findMaxApprovedAmount(financialCapacityFactor, amount, MAX_PURCHASE_AMOUNT, periodInMonths);
            return buildPurchaseApprovalResult(maxAmount, periodInMonths, PurchaseStatus.APPROVED, true);
        }

        // 2. Scenario when requested amount is rejected, but we want to approve the maximum amount within the range
        // e.g 500e -> rejected, 300e -> approved
        var maxApprovedAmountInRange = findMaxApprovedAmount(financialCapacityFactor, BigDecimal.ONE, amount, periodInMonths);
        if (maxApprovedAmountInRange.compareTo(BigDecimal.ONE) > 0) {
            return buildPurchaseApprovalResult(
                maxApprovedAmountInRange,
                periodInMonths,
                PurchaseStatus.APPROVED_WITH_AMOUNT_RANGE,
                true
            );
        }

        // 3. Let's try to extend the payment period
        var maxApprovedPeriodInMonths = findMaxApprovedPeriod(financialCapacityFactor, amount, periodInMonths);
        if (maxApprovedPeriodInMonths != null) {
            return buildPurchaseApprovalResult(
                amount,
                maxApprovedPeriodInMonths,
                PurchaseStatus.APPROVED_WITH_EXTENDED_PAYMENT_PERIOD,
                true
            );
        }

        // 4. Nothing was approved
        return buildPurchaseApprovalResult(amount, periodInMonths, PurchaseStatus.REJECTED, false);
    }

    private BigDecimal findMaxApprovedAmount(Integer factor, BigDecimal startAmount, BigDecimal maxAmount, Integer periodInMonths) {
        // Use a binary search to find the max amount
        var left = startAmount;
        var right = maxAmount;
        var result = startAmount;

        while (left.compareTo(right) <= 0) {
            // Calculate the mid amount (e.g (1000.00 + 5000.00) / 2 = 3000.00)
            var mid = left.add(right).divide(DIVISOR, ROUNDING_SCALE, ROUNDING_MODE);

            // If score is approved (e.g 3000.00), then we try to go up while saving approved score as the best candidate for max amount
            // If not (e.g 3000.01), then we go down while still holding the best candidate for max amount
            // Continue doing until left > right
            if (isApproved(calculateApprovalScore(factor, mid, periodInMonths))) {
                result = mid;
                left = mid.add(MIN_AMOUNT_INCREMENT);
            } else {
                right = mid.subtract(MIN_AMOUNT_INCREMENT);
            }
        }

        return result.setScale(ROUNDING_SCALE, RoundingMode.DOWN);
    }

    private static Integer findMaxApprovedPeriod(Integer factor, BigDecimal amount, Integer initialPeriodInMonths) {
        for (int period = initialPeriodInMonths; period <= MAX_PURCHASE_PERIOD_IN_MONTHS; period++) {
            double score = calculateApprovalScore(factor, amount, period);

            if (isApproved(score)) {
                return period;
            }
        }

        return null;
    }

    public static double calculateApprovalScore(Integer financialCapacityFactor, BigDecimal amount, Integer paymentPeriod) {
        var factor = BigDecimal.valueOf(financialCapacityFactor);
        var period = BigDecimal.valueOf(paymentPeriod);

        // Round half down used for more strict calculation
        return factor.divide(amount, ROUNDING_SCALE, RoundingMode.HALF_DOWN)
            .multiply(period)
            .doubleValue();
    }

    private static boolean isApproved(double approvalScore) {
        return approvalScore >= APPROVAL_THRESHOLD;
    }
}
