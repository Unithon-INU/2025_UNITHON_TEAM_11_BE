package Uniton.Fring.domain.recipe.repository;

import Uniton.Fring.domain.recipe.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findTop10ByOrderByRatingDesc();

    Page<Recipe> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<Recipe> findByMemberId(Long memberId);
}
