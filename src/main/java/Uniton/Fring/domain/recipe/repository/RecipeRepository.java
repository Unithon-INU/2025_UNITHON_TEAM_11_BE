package Uniton.Fring.domain.recipe.repository;

import Uniton.Fring.domain.recipe.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    Page<Recipe> findByMemberId(Long memberId, Pageable pageable);

    List<Recipe> findTop5ByOrderByRatingDesc();

    List<Recipe> findTop5ByOrderByCreatedAtDesc();

    List<Recipe> findTop10ByOrderByRatingDesc();

    Page<Recipe> findAll(Pageable pageable);

    Optional<Recipe> findTop1ByTitleContainingOrderByCreatedAtDesc(String keyword);

    // 키워드를 기반으로 유사도 정렬 쿼리문
    @Query(value = """
        SELECT * FROM recipe r
        WHERE r.title LIKE CONCAT('%', :keyword, '%')
        ORDER BY
            CASE
                WHEN r.title = :keyword THEN 0
                WHEN r.title LIKE CONCAT(:keyword, '%') THEN 1
                WHEN r.title LIKE CONCAT('%', :keyword) THEN 2
                WHEN r.title LIKE CONCAT('%', :keyword, '%') THEN 3
                ELSE 4
            END
        """,
            countQuery = "SELECT COUNT(*) FROM recipe r WHERE r.title LIKE CONCAT('%', :keyword, '%')",
            nativeQuery = true)
    Page<Recipe> findByTitleContaining(@Param("keyword") String title, Pageable pageable);
}
