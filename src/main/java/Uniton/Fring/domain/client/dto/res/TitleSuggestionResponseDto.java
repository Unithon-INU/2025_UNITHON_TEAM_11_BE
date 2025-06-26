package Uniton.Fring.domain.client.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "상품 제목 추천 응답 DTO")
public class TitleSuggestionResponseDto {

    @Schema(description = "추천된 상품 제목", example = "토마토 행사")
    private String generated_title;
}
