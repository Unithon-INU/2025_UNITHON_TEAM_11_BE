package Uniton.Fring.domain.recipe.dto.res;

import Uniton.Fring.domain.recipe.entity.RecipeStep;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "레시피 순서 응답 DTO")
public class RecipeStepResponseDto {

    @Schema(description = "요리 순서", example = "1")
    private final int stepOrder;

    @Schema(description = "요리 설명", example = "팬에 기름을 두르고 양파를 볶아주세요.")
    private final String description;

    @Schema(description = "요리 이미지 URL", example = "https://fring-s3.s3.ap-northeast-2.amazonaws.com/recipes/image.jpg")
    private final String imageUrl;

    @Builder
    private RecipeStepResponseDto(RecipeStep recipeStep) {
        this.stepOrder = recipeStep.getStepOrder();
        this.description = recipeStep.getDescription();
        this.imageUrl = recipeStep.getImageUrl();
    }
}
