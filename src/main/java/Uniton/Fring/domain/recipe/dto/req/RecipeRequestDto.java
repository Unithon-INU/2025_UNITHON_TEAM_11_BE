package Uniton.Fring.domain.recipe.dto.req;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class RecipeRequestDto {

    private String title;

    private String content;

    private String imageUrl;

    private Double rating;

    private int headCount;

    private String cookingTime;

    private String difficulty;

    private Map<String, String> ingredients;

    private Map<String, String> sauces;

    private List<RecipeStepRequestDto> steps;
}
