package Uniton.Fring.domain.product.repository;

import Uniton.Fring.domain.product.entity.RecentProductView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecentProductViewRepository extends JpaRepository<RecentProductView, Long> {

    Page<RecentProductView> findByMemberId(Long memberId, Pageable pageable);

    Optional<RecentProductView> findByMemberIdAndProductId(Long memberId, Long productId);

    default void saveOrUpdate(Long memberId, Long productId) {
        this.findByMemberIdAndProductId(memberId, productId).ifPresentOrElse(
                existing -> {
                    existing.updateViewTime();
                    save(existing);
                },
                () -> save(new RecentProductView(memberId, productId))
        );
    }
}
