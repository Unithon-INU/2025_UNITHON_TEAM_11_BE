package Uniton.Fring.domain.client.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "상품 옵션 요청 DTO")
public class TitleSuggestionRequestDto {

    @Schema(description = "상품 옵션", example = "방울 토마토, 왕토마토")
    private List<String> options;

    @Schema(description = "원산지", example = "강원도 평창")
    private String origin;

    @Schema(description = "수확시기", example = "2025-01-15")
    private String harvestPeriod;
}
