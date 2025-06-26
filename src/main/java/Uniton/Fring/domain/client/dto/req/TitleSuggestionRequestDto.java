package Uniton.Fring.domain.client.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "상품 추천 제목 요청 DTO")
public class TitleSuggestionRequestDto {

    @Schema(description = "상품 요약 설명", example = "신선하고 맛 좋은 토마토")
    private String description;

    @Schema(description = "원산지", example = "강원도 평창")
    private String origin;

    @Schema(description = "수확시기", example = "2025-01-15")
    private String harvestPeriod;

    @Schema(description = "스프링 서버 주소")
    private String backendUrl;

    @Builder
    private TitleSuggestionRequestDto(String description, String origin, String harvestPeriod, String backendUrl) {
        this.description = description;
        this.origin = origin;
        this.harvestPeriod = harvestPeriod;
        this.backendUrl = backendUrl;
    }
}
