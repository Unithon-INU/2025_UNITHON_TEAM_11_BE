package Uniton.Fring.domain.review.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "농수산품 리뷰 추가 요청 DTO")
public class ProductReviewRequestDto {

    @Schema(description = "농수산품 아이디", example = "1")
    private Long productId;

    @Schema(description = "농수산품 옵션", example = "단품 계란 30구, 1판")
    private String purchase_option;

    @Schema(description = "농수산품 별점", example = "4.0")
    private Integer rating;

    @Schema(description = "후기", example = "정말 신선해요 !!")
    private String content;

    @Schema(description = "주문 아이디", example = "1")
    private Long purchaseId;
}
