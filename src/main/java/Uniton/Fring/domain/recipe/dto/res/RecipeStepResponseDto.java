package Uniton.Fring.domain.recipe.dto.res;

import Uniton.Fring.domain.recipe.entity.RecipeStep;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RecipeStepResponseDto {

    private final int stepOrder;

    private final String description;

    private final String imageUrl;

    @Builder
    private RecipeStepResponseDto(RecipeStep recipeStep) {
        this.stepOrder = recipeStep.getStepOrder();
        this.description = recipeStep.getDescription();
        this.imageUrl = recipeStep.getImageUrl();
    }
}
