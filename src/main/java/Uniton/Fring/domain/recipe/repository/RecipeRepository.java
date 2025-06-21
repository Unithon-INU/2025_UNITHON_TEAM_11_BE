package Uniton.Fring.domain.recipe.repository;

import Uniton.Fring.domain.recipe.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    Page<Recipe> findByMemberId(Long memberId, Pageable pageable);

    List<Recipe> findTop5ByOrderByRatingDesc();

    List<Recipe> findTop5ByOrderByCreatedAtDesc();

    List<Recipe> findTop10ByOrderByRatingDesc();

    Page<Recipe> findAll(Pageable pageable);

    Optional<Recipe> findTop1ByTitleContainingOrderByCreatedAtDesc(String keyword);

    Boolean existsByMemberIdAndId(Long memberId, Long recipeId);
}
