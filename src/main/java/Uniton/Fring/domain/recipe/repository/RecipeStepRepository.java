package Uniton.Fring.domain.recipe.repository;

import Uniton.Fring.domain.recipe.entity.RecipeStep;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecipeStepRepository extends JpaRepository<RecipeStep, Long> {
    List<RecipeStep> findByRecipeId(Long recipeId);
    void deleteByRecipeId(Long recipeId);

    @Query("SELECT r.imageUrl FROM RecipeStep r WHERE r.recipeId = :recipeId")
    List<String> findImageUrlsByRecipeId(@Param("recipeId") Long recipeId);
}
