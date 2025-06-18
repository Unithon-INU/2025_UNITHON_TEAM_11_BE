package Uniton.Fring.domain.like;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {

    boolean existsByMemberIdAndProductId(Long memberId, Long productId);
}
