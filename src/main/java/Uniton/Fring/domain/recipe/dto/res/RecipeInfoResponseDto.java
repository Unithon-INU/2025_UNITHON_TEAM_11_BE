package Uniton.Fring.domain.recipe.dto.res;

import Uniton.Fring.domain.recipe.entity.Recipe;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
public class RecipeInfoResponseDto {

    private final String title;

    private final String content;

    private final String imageUrl;

    private final Double rating;

    private final int headCount;

    private final String cookingTime;

    private final String difficulty;

    private final LocalDateTime createdAt;

    private final Map<String, String> ingredients;

    private final Map<String, String> sauces;

    private final List<RecipeStepResponseDto> steps;

    @Builder
    private RecipeInfoResponseDto(Recipe recipe, List<RecipeStepResponseDto> recipeStep) {
        this.title = recipe.getTitle();
        this.content = recipe.getContent();
        this.imageUrl = recipe.getImageUrl();
        this.rating = recipe.getRating();
        this.headCount = recipe.getHeadCount();
        this.cookingTime = recipe.getCookingTime();
        this.difficulty = recipe.getDifficulty();
        this.createdAt = recipe.getCreatedAt();
        this.ingredients = recipe.getIngredients();
        this.sauces = recipe.getSauces();
        this.steps = recipeStep;
    }
}
