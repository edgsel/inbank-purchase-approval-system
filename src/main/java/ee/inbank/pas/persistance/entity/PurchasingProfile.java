package ee.inbank.pas.persistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@Table(schema = "PAS", name = "PURCHASING_PROFILE")
public class PurchasingProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "FINANCIAL_CAPACITY_FACTOR")
    private Integer financialCapacityFactor;

    @Column(name = "CREATED_AT", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "MODIFIED_AT", nullable = false)
    @LastModifiedDate
    private LocalDateTime modifiedAt;
}
