package ee.inbank.pas.persistance.repository;

import ee.inbank.pas.persistance.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}
