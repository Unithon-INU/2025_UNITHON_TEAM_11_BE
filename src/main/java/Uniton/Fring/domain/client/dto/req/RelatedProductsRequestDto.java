package Uniton.Fring.domain.client.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "연관된 상품 요청 DTO")
public class RelatedProductsRequestDto {

    @Schema(description = "상품 Id", example = "1")
    private Long id;

    @Schema(description = "상품 이름", example = "계란 30구, 1판")
    private String name;

    @Builder
    private RelatedProductsRequestDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
