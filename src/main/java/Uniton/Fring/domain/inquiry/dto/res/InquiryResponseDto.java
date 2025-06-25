package Uniton.Fring.domain.inquiry.dto.res;

import Uniton.Fring.domain.inquiry.Inquiry;
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

    @NotNull
    @Schema(description = "상품 Id", example = "1")
    private final Long productId;

    @NotBlank
    @Schema(description = "문의 제목", example = "맛있는 달걀인가요?")
    private final String title;

    @NotBlank
    @Schema(description = "문의 내용", example = "저는 맛이 없으면 안먹어요.")
    private final String content;

    @Schema(description = "문의 이미지")
    private final List<String> imageUrls;

    @Builder
    private InquiryResponseDto(Inquiry inquiry) {
        this.id = inquiry.getId();
        this.memberId = inquiry.getMemberId();
        this.productId = inquiry.getProductId();
        this.title = inquiry.getTitle();
        this.content = inquiry.getContent();
        this.imageUrls = inquiry.getImageUrls();
    }
}
