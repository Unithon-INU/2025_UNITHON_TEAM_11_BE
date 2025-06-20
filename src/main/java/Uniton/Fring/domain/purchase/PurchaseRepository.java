package Uniton.Fring.domain.purchase;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    Optional<Purchase> findTopByMemberIdAndProductIdOrderByPurchaseDateDesc(Long memberId, Long productId);
}
