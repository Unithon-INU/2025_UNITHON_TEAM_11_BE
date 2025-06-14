package Uniton.Fring.domain.recipe.dto.res;

import Uniton.Fring.domain.recipe.entity.Recipe;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BestRecipeResponseDto {

    private final Long recipeId;
    private final String imageUrl;
    private final String nickname;
    private final String title;
    private final String cookingTime;
    private final Double rating;

    @Builder
    private BestRecipeResponseDto(Recipe recipes, String nickname) {
        this.recipeId = recipes.getId();
        this.imageUrl = recipes.getImageUrl();
        this.nickname = nickname;
        this.title = recipes.getTitle();
        this.cookingTime = recipes.getCookingTime();
        this.rating = recipes.getRating();
    }
}
