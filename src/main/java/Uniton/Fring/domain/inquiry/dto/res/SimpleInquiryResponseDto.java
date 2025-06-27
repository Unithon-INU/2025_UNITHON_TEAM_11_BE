package Uniton.Fring.domain.inquiry.dto.res;

import Uniton.Fring.domain.inquiry.entity.Inquiry;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Schema(description = "문의 정보 응답 DTO")
public class SimpleInquiryResponseDto {

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
    @Schema(description = "상태", example = "답변완료")
    private final String status;

    @NotBlank
    @Schema(description = "날짜", example = "2025-01--1")
    private final LocalDate date;

    @Schema(description = "문의 이미지")
    private final String imageUrl;

    @Builder
    private SimpleInquiryResponseDto(Inquiry inquiry, String imageUrl) {
        this.id = inquiry.getId();
        this.memberId = inquiry.getMemberId();
        this.productId = inquiry.getProductId();
        this.title = inquiry.getTitle();
        this.status = inquiry.getStatus().getDescription();
        this.date = inquiry.getCreatedAt();
        this.imageUrl = imageUrl;
    }
}
