package ee.inbank.pas.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PurchaseApprovalResult(
    BigDecimal amount,
    Integer paymentPeriodInMonths,
    PurchaseStatus purchaseStatus,
    boolean approved
) {
}
