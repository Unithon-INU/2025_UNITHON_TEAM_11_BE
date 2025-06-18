package Uniton.Fring.domain.recipe.dto.res;

import Uniton.Fring.domain.recipe.entity.Recipe;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Schema(description = "레시피 상세 정보 응답 DTO")
public class RecipeInfoResponseDto {

    @Schema(description = "레시피 제목", example = "차돌된장찌개")
    private final String title;

    @Schema(description = "레시피 설명", example = "차돌박이를 이용한 구수한 된장찌개 레시피입니다.")
    private final String content;

    @Schema(description = "레시피 대표 이미지 URL", example = "https://fring-s3.s3.ap-northeast-2.amazonaws.com/recipes/image.jpg")
    private final String imageUrl;

    @Schema(description = "레시피 평점", example = "4.7")
    private final Double rating;

    @Schema(description = "인분 수", example = "2")
    private final int headCount;

    @Schema(description = "조리 시간", example = "20분")
    private final String cookingTime;

    @Schema(description = "난이도", example = "중")
    private final String difficulty;

    @Schema(description = "레시피 생성 일시", example = "2024-06-17T14:30:00")
    private final LocalDateTime createdAt;

    @Schema(description = "재료 목록", example = "{\"두부\":\"1모\", \"된장\":\"2스푼\"}")
    private final Map<String, String> ingredients;

    @Schema(description = "양념 목록", example = "{\"간장\":\"1스푼\", \"다진마늘\":\"1스푼\"}")
    private final Map<String, String> sauces;

    @Schema(description = "요리 순서 목록")
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
