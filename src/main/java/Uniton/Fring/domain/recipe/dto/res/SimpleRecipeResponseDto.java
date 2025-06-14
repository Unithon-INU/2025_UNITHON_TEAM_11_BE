package Uniton.Fring.domain.recipe.dto.res;

import Uniton.Fring.domain.recipe.entity.Recipe;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SimpleRecipeResponseDto {

    private final Long recipeId;
    private final String imageUrl;
    private final String title;
    private final String cookingTime;
    private final Double rating;

    @Builder
    public SimpleRecipeResponseDto(Recipe recipe) {
        this.recipeId = recipe.getId();
        this.imageUrl = recipe.getImageUrl();
        this.title = recipe.getTitle();
        this.cookingTime = recipe.getCookingTime();
        this.rating = recipe.getRating();
    }
}