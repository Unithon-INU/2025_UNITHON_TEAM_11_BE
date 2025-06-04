package Uniton.Fring.domain.recipe.repository;

import Uniton.Fring.domain.recipe.entity.RecipeStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeStepRepository extends JpaRepository<RecipeStep, Long> {
    List<RecipeStep> findByRecipeId(Long recipeId);
    void deleteByRecipeId(Long recipeId);
}
