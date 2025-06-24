package Uniton.Fring.domain.cart;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByMemberIdAndProductId(Long memberId, Long productId);

    List<Cart> findByMemberId(Long memberId);

    void deleteByMemberId(Long memberId);
}
