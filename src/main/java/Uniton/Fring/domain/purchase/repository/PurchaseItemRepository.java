package Uniton.Fring.domain.purchase.repository;

import Uniton.Fring.domain.purchase.entity.PurchaseItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Long> {

    List<PurchaseItem> findByPurchaseIdIn(List<Long> purchaseIds);
}
