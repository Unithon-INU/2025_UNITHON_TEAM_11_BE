package Uniton.Fring.domain.client.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "연관된 상품 요청 DTO")
public class RelatedProductsRequestDto {

    @Schema(description = "상품 이름", example = "계란 30구, 1판")
    private String name;

    @Schema(description = "원산지", example = "강원도 평창")
    private String origin;

    @Schema(description = "수확시기", example = "2025-01-15")
    private String harvestPeriod;
}
