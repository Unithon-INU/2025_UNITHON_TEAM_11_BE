package Uniton.Fring.domain.inquiry.dto.res;

import Uniton.Fring.domain.inquiry.entity.Inquiry;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "문의 정보 응답 DTO")
public class InquiryResponseDto {

    @NotNull
    @Schema(description = "문의 Id", example = "1")
    private final Long id;

    @NotNull
    @Schema(description = "회원 Id", example = "1")
    private final Long memberId;

    @Schema(description = "회원 닉네임")
    private final String memberNickname;

    @NotNull
    @Schema(description = "상품 Id", example = "1")
    private final Long productId;

    @Schema(description = "상품 이미지")
    private final String productImageUrl;

    @NotBlank
    @Schema(description = "문의 제목", example = "맛있는 달걀인가요?")
    private final String title;

    @NotBlank
    @Schema(description = "문의 내용", example = "저는 맛이 없으면 안먹어요.")
    private final String content;

    @Schema(description = "문의 이미지")
    private final List<String> imageUrls;

    @Schema(description = "답변 내용", example = "안녕하세요 고객님.")
    private final AnswerResponseDto answer;

    @Builder
    private InquiryResponseDto(Inquiry inquiry, String memberNickname, String productImageUrl, AnswerResponseDto answer) {
        this.id = inquiry.getId();
        this.memberId = inquiry.getMemberId();
        this.memberNickname = memberNickname;
        this.productId = inquiry.getProductId();
        this.productImageUrl = productImageUrl;
        this.title = inquiry.getTitle();
        this.content = inquiry.getContent();
        this.imageUrls = inquiry.getImageUrls();
        this.answer = answer;
    }
}
