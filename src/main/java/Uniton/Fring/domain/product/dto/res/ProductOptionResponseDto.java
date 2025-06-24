package Uniton.Fring.domain.product.dto.res;

import Uniton.Fring.domain.product.entity.ProductOption;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "상품 옵션 정보 응답 DTO")
public class ProductOptionResponseDto {

    @Schema(description = "옵션 ID", example = "1")
    private final Long id;

    @Schema(description = "상품 ID", example = "1")
    private final Long productId;

    @Schema(description = "옵션 이름", example = "대용량")
    private final String optionName;

    @Schema(description = "옵션 추가 가격", example = "1500")
    private final Integer additionalPrice;

    @Builder
    private ProductOptionResponseDto(ProductOption productOption) {
        this.id = productOption.getId();
        this.productId = productOption.getProductId();
        this.optionName = productOption.getOptionName();
        this.additionalPrice = productOption.getAdditionalPrice();
    }
}