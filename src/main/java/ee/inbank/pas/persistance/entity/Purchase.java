package ee.inbank.pas.persistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name = "MAX_ALLOWED_AMOUNT")
    private BigDecimal maxAllowedAmount;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private PurchaseStatus status;

    @Column(name = "CREATED_AT", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "MODIFIED_AT", nullable = false)
    @LastModifiedDate
    private LocalDateTime modifiedAt;

    public enum PurchaseStatus {
        APPROVED,
        APPROVED_PARTIALLY,
        REJECTED
    }
}
