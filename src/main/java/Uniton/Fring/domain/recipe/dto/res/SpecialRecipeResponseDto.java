package Uniton.Fring.domain.recipe.dto.res;

import Uniton.Fring.domain.recipe.entity.Recipe;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "특별한 날 좋은 레시피 응답 DTO")
public class SpecialRecipeResponseDto {

    @Schema(description = "레시피 ID", example = "1")
    private Long id;

    @Schema(description = "레시피 제목", example = "고소한 참치 마요 덮밥")
    private String title;

    @Schema(description = "레시피 내용", example = "안녕하세요! 오늘은 토마토를 이용한 요리를 해보려고 합니다.")
    private String content;

    @Schema(description = "레시피 대표 이미지 URL", example = "https://fring-s3.s3.ap-northeast-2.amazonaws.com/recipes/image.jpg")
    private String image;

    @Schema(description = "조리 시간", example = "15분")
    private String time;

    @Schema(description = "레시피 평점", example = "4.8")
    private Double rating;

    @Schema(description = "댓글 개수", example = "12")
    private Integer comment;

    @Builder
    private SpecialRecipeResponseDto(Recipe recipe, Integer commentCount) {
        this.id = recipe.getId();
        this.title = recipe.getTitle();
        this.content = recipe.getContent();
        this.image = recipe.getImageUrl();
        this.time = recipe.getCookingTime();
        this.rating = recipe.getRating();
        this.comment = commentCount;
    }
}
