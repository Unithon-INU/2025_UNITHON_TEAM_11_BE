package Uniton.Fring.domain.review.repository;

import Uniton.Fring.domain.review.entity.Review;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    int countByRecipeId(Long recipeId);

    Page<Review> findByProductId(Long productId, Pageable pageable);
    Page<Review> findByRecipeId(Long recipeId, Pageable pageable);

    int countByProductId(Long productId);

    List<Review> findTop5ByProductIdOrderByCreatedAtDesc(Long productId);

    // 리뷰 수 조회
    @Query("SELECT r.recipeId AS recipeId, COUNT(r) AS cnt FROM Review r WHERE r.recipeId IN :recipeIds GROUP BY r.recipeId")
    List<Object[]> countReviewsByRecipeIds(@Param("recipeIds") List<Long> recipeIds);

    @Query("SELECT COUNT(ri) FROM Review r JOIN r.imageUrls ri WHERE r.productId = :productId")
    int countTotalImagesByProductId(@Param("productId") Long productId);

    // 작성일 기준 최신 리뷰의 이미지 5장 반환
    @Query("""
        SELECT ri 
        FROM Review r 
        JOIN r.imageUrls ri 
        WHERE r.id IN :reviewIds 
        ORDER BY r.createdAt DESC
    """)
    List<String> findTopImageUrlsByReviewIds(@Param("reviewIds") List<Long> reviewIds, Pageable pageable);

    boolean existsByMemberIdAndRecipeId(Long memberId, Long recipeId);
}
