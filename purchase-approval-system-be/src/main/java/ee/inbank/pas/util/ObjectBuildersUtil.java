package ee.inbank.pas.util;

import ee.inbank.model.CustomerPurchaseResponse;
import ee.inbank.pas.dto.PurchaseApprovalResult;
import ee.inbank.pas.dto.PurchaseStatus;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class ObjectBuildersUtil {

    public static CustomerPurchaseResponse buildResponse(PurchaseApprovalResult approvalResult) {
        return CustomerPurchaseResponse.builder()
            .amount(approvalResult.amount())
            .paymentPeriodInMonths(approvalResult.paymentPeriodInMonths())
            .approved(approvalResult.approved())
            .build();
    }

    public static PurchaseApprovalResult buildPurchaseApprovalResult(
        BigDecimal amount, Integer periodInMonths,
        PurchaseStatus status, boolean approved
    ) {
        return PurchaseApprovalResult.builder()
            .amount(amount)
            .paymentPeriodInMonths(periodInMonths)
            .purchaseStatus(status)
            .approved(approved)
            .build();
    }
}
