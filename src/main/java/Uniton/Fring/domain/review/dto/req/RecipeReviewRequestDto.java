package Uniton.Fring.domain.review.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "레시피 리뷰 추가 요청 DTO")
public class RecipeReviewRequestDto {

    @Schema(description = "레시피 아이디", example = "1")
    private Long recipeId;

    @Schema(description = "레시피 별점", example = "4.0")
    private Double rating;

    @Schema(description = "후기", example = "야미~ 맛있었어요 !")
    private String content;
}
