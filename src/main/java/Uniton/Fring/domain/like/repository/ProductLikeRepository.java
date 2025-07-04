package Uniton.Fring.domain.like.repository;

import Uniton.Fring.domain.like.entity.ProductLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {

    boolean existsByMemberIdAndProductId(Long memberId, Long productId);

    void deleteByMemberIdAndProductId(Long memberId, Long productId);

    List<ProductLike> findByMemberIdAndProductIdIn(Long memberId, List<Long> productIds);

    Page<ProductLike> findByMemberId(Long memberId, Pageable pageable);

    long countByMemberId(Long memberId);
}
