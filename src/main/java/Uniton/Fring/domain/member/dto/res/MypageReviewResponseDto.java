package Uniton.Fring.domain.member.dto.res;

import Uniton.Fring.domain.review.dto.res.ReviewResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "내 리뷰 목록 응답 DTO")
public class MypageReviewResponseDto {

    @Schema(description = "상품 리뷰 목록")
    private final List<ReviewResponseDto> productReviews;

    @Schema(description = "레시피 리뷰 목록")
    private final List<ReviewResponseDto> recipeReviews;

    @Builder
    private MypageReviewResponseDto(List<ReviewResponseDto> productReviews, List<ReviewResponseDto> recipeReviews) {
        this.productReviews = productReviews;
        this.recipeReviews = recipeReviews;
    }
}
