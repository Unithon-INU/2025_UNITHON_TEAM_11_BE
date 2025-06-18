package Uniton.Fring.domain.product.repository;

import Uniton.Fring.domain.product.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findTop5ByOrderByDiscountRateDesc();

    List<Product> findTop5ByOrderByRatingDesc();

    List<Product> findTop10ByOrderByRatingDesc();

    // Product 엔티티에서 p라는 별칭으로 모든 컬럼을 조회
    // Product 테이블과 Review 테이블을 조인
    // GROUP BY를 통해 상품 단위로 묶고 집계 함수(COUNT)를 쓸 수 있게 함
    // 리뷰 수가 많은 상품부터 정렬
    @Query("""
        SELECT p FROM Product p
        LEFT JOIN Review r ON p.id = r.productId
        GROUP BY p.id
        ORDER BY COUNT(r) DESC
    """)
    List<Product> findTopProductsByReviewCount(Pageable pageable);

    Boolean existsByMemberIdAndId(Long memberId, Long productId);
}
