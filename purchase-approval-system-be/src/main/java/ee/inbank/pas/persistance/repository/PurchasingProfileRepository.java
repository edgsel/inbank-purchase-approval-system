package ee.inbank.pas.persistance.repository;

import ee.inbank.pas.persistance.entity.PurchasingProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchasingProfileRepository extends JpaRepository<PurchasingProfile, Long> {
}
