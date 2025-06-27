package Uniton.Fring.domain.inquiry.dto.res;

import Uniton.Fring.domain.inquiry.entity.Inquiry;
import Uniton.Fring.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "문의 답변 정보 응답 DTO")
public class AnswerResponseDto {

    @NotNull
    @Schema(description = "문의 Id", example = "1")
    private final Long id;

    @NotNull
    @Schema(description = "회원 Id", example = "1")
    private final Long memberId;

    @NotBlank
    @Schema(description = "회원 닉네임", example = "고릴라 농장")
    private final String memberNickname;

    @NotNull
    @Schema(description = "상품 Id", example = "1")
    private final Long productId;

    @NotBlank
    @Schema(description = "답변 제목", example = "안녕하세요")
    private final String title;

    @NotBlank
    @Schema(description = "답변 내용", example = "안녕하세요 고객님.")
    private final String content;

    @Builder
    private AnswerResponseDto(Inquiry inquiry, Member member) {
        this.id = inquiry.getId();
        this.memberId = member.getId();
        this.memberNickname = member.getNickname();
        this.productId = inquiry.getProductId();
        this.title = inquiry.getAnswerTitle();
        this.content = inquiry.getAnswerContent();
    }
}
