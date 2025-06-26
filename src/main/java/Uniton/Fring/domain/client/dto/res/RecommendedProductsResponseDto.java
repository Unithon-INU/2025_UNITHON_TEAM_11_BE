package Uniton.Fring.domain.client.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "연관 상품 목록 조회 응답 DTO")
public class RecommendedProductsResponseDto {

    @Schema(description = "원본 상품 Id", example = "1")
    private Long sourceProductId;

    @Schema(description = "연관 상품 Id", example = "[1,2,3]")
    private List<Long> recommendedProductIds;
}
