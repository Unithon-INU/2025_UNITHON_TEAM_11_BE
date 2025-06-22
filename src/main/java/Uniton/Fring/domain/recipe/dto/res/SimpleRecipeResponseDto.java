package Uniton.Fring.domain.recipe.dto.res;

import Uniton.Fring.domain.recipe.entity.Recipe;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "상위 평점 레시피 응답 DTO")
public class SimpleRecipeResponseDto {

    @Schema(description = "레시피 ID", example = "1")
    private final Long id;

    @Schema(description = "레시피 제목", example = "고소한 참치 마요 덮밥")
    private final String title;

    @Schema(description = "레시피 대표 이미지 URL", example = "https://fring-s3.s3.ap-northeast-2.amazonaws.com/recipes/image.jpg")
    private final String image;

    @Schema(description = "조리 시간", example = "15분")
    private final String time;

    @Schema(description = "레시피 평점", example = "4.8")
    private final Double rating;

    @Schema(description = "찜 여부", example = "true")
    private final Boolean isLiked;

    @Schema(description = "댓글 개수", example = "12")
    private final Integer comment;

    @Builder
    private SimpleRecipeResponseDto(Recipe recipe, Boolean isLiked, Integer reviewCount) {
        this.id = recipe.getId();
        this.title = recipe.getTitle();
        this.image = recipe.getImageUrl();
        this.time = recipe.getCookingTime();
        this.rating = recipe.getRating();
        this.isLiked = isLiked;
        this.comment = reviewCount;
    }
}
