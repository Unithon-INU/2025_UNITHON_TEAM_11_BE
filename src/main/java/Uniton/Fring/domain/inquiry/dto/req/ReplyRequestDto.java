package Uniton.Fring.domain.inquiry.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "문의 요청 DTO ")
public class ReplyRequestDto {

    @NotBlank
    @Schema(description = "답변 제목", example = "안녕하세요")
    private String title;

    @NotBlank
    @Schema(description = "답변 내용", example = "안녕하세요. 저희 달걀은...")
    private String content;
}
