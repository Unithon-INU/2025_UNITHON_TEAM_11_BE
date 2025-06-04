package Uniton.Fring.domain.recipe.dto.req;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecipeStepRequestDto {

    private int stepOrder;

    private String description;

    private String imageUrl;
}
