package Uniton.Fring.domain.review.dto.res;

import Uniton.Fring.domain.member.dto.res.MemberInfoResponseDto;
import Uniton.Fring.domain.review.entity.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Schema(description = "리뷰 정보 응답 DTO")
public class ReviewResponseDto {

    @Schema(description = "회원", example = "dor****")
    private final MemberInfoResponseDto memberInfo;

    @Schema(description = "리뷰 내용", example = "아침마다 계란을 삶아먹는데 여기만한 계란이 없더라구요.^^")
    private final String content;

    @Schema(description = "리뷰 이미지 URL 목록", example = "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]")
    private final List<String> imageUrls;

    @Schema(description = "리뷰 평점", example = "4.5")
    private final Double rating;

    @Schema(description = "리뷰 작성 일자", example = "2025-01-23")
    private final LocalDate createdAt;

    @Schema(description = "리뷰 좋아요 수", example = "2")
    private final Integer likeCount;

    @Schema(description = "구매 상품 옵션", example = "단품 계란 30구, 1판")
    private final String purchaseOption;

    @Builder
    private ReviewResponseDto(MemberInfoResponseDto memberInfo, Review review, String purchaseOption) {
        this.memberInfo = memberInfo;
        this.content = review.getContent();
        this.imageUrls = review.getImageUrls();
        this.rating = review.getRating();
        this.createdAt = review.getCreatedAt();
        this.likeCount = review.getLikeCount();
        this.purchaseOption = purchaseOption;
    }
}
