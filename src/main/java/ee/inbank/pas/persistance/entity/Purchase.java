package ee.inbank.pas.persistance.entity;

import ee.inbank.pas.dto.PurchaseApprovalResult;
import ee.inbank.pas.dto.PurchaseStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(schema = "PUBLIC", name = "PURCHASE")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "CUSTOMER_ID")
    private Customer customer;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "MAX_APPROVED_AMOUNT")
    private BigDecimal maxApprovedAmount;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private PurchaseStatus status;

    @CreatedDate
    @Column(name = "CREATED_AT", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "MODIFIED_AT", nullable = false)
    private LocalDateTime modifiedAt;

    public static Purchase toEntity(Customer customer,PurchaseApprovalResult approvalResult, BigDecimal requestedAmount) {
        return Purchase.builder()
            .customer(customer)
            .amount(requestedAmount)
            .maxApprovedAmount(approvalResult.amount())
            .status(approvalResult.purchaseStatus())
            .build();
    }
}
