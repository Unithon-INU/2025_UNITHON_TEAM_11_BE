package Uniton.Fring.domain.purchase.repository;

import Uniton.Fring.domain.purchase.entity.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    Page<Purchase> findByMemberId(Long memberId, Pageable pageable);
}
