package Uniton.Fring.domain.inquiry.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "문의 요청 DTO ")
public class InquiryRequestDto {

    @NotBlank
    @Schema(description = "문의 제목", example = "맛있는 달걀인가요?")
    private String title;

    @NotBlank
    @Schema(description = "문의 내용", example = "저는 맛이 없으면 안먹어요.")
    private String content;
}
