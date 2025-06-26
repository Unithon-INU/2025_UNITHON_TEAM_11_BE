package Uniton.Fring.domain.product.repository;

import Uniton.Fring.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByMemberId(Long memberId, Pageable pageable);

    List<Product> findTop5ByOrderByDiscountRateDesc();

    List<Product> findTop5ByOrderByRatingDesc();

    List<Product> findTop10ByOrderByRatingDesc();

    Page<Product> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

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

    List<Product> findTop5ByOrderByLikeCountDesc();

    // 키워드를 기반으로 유사도 정렬 쿼리문
    @Query(value = """
        SELECT * FROM product p
        WHERE p.name LIKE CONCAT('%', :keyword, '%')
        ORDER BY
            CASE
                WHEN p.name = :keyword THEN 0
                WHEN p.name LIKE CONCAT(:keyword, '%') THEN 1
                WHEN p.name LIKE CONCAT('%', :keyword) THEN 2
                WHEN p.name LIKE CONCAT('%', :keyword, '%') THEN 3
                ELSE 4
            END
        """,
            countQuery = "SELECT COUNT(*) FROM product p WHERE p.name LIKE CONCAT('%', :keyword, '%')",
            nativeQuery = true)
    Page<Product> findByNameContaining(@Param("keyword") String name, Pageable pageable);
}
