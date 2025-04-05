package ee.inbank.pas.service;

import ee.inbank.model.CustomerPurchaseRequest;
import ee.inbank.pas.dto.PurchaseApprovalResult;
import ee.inbank.pas.dto.PurchaseStatus;
import ee.inbank.pas.exception.EntityNotFoundException;
import ee.inbank.pas.exception.ErrorCode;
import ee.inbank.pas.persistance.entity.Customer;
import ee.inbank.pas.persistance.entity.Purchase;
import ee.inbank.pas.persistance.repository.CustomerRepository;
import ee.inbank.pas.persistance.repository.PurchaseRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static ee.inbank.pas.util.ObjectBuildersUtil.buildPurchaseApprovalResult;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class PurchaseApprovalService {

    private static final Set<Long> HARDCODED_PERSONAL_IDS_WITH_ADJUSTED_PAYMENT_PERIOD = Set.of(12345678912L);

    private final CustomerRepository customerRepository;
    private final PurchaseRepository purchaseRepository;
    private final PurchaseApprovalCalculationService purchaseApprovalCalculationService;

    public PurchaseApprovalResult getPurchaseApprovalResult(CustomerPurchaseRequest request) {
        var customer = findCustomerOrThrow(request.getPersonalId());
        var approvalResult = calculateApprovalResult(customer, request);

        log.info("Calculated purchase approval result for customer ID: {}", customer.getId());

        persistResult(customer, approvalResult, request.getAmount());

        return approvalResult;
    }

    private PurchaseApprovalResult calculateApprovalResult(Customer customer, CustomerPurchaseRequest request) {
        if (HARDCODED_PERSONAL_IDS_WITH_ADJUSTED_PAYMENT_PERIOD.contains(customer.getPersonalId())) {
            log.warn("Applying hardcoded payment period for customer ID: {}", customer.getId());
            return buildPurchaseApprovalResult(request.getAmount(), 24, PurchaseStatus.APPROVED, true);
        }

        if (!customer.isEligible()) {
            return buildPurchaseApprovalResult(
                request.getAmount(), request.getPaymentPeriodInMonths(),
                PurchaseStatus.REJECTED, false
            );
        }

        return purchaseApprovalCalculationService.getApprovalResult(
            customer.getPurchasingProfile().getFinancialCapacityFactor(),
            request.getAmount(),
            request.getPaymentPeriodInMonths()
        );
    }

    private Customer findCustomerOrThrow(Long personalId) {
        return Optional.ofNullable(customerRepository.findByPersonalId(personalId))
            .orElseThrow(() -> new EntityNotFoundException(
                "Customer with personal ID %s not found".formatted(personalId),
                ErrorCode.CUSTOMER_NOT_FOUND.name()));
    }

    private void persistResult(Customer customer, PurchaseApprovalResult approvalResult, BigDecimal requestedAmount) {
        purchaseRepository.save(Purchase.toEntity(customer, approvalResult, requestedAmount));
    }
}
