package Uniton.Fring.domain.product.dto.res;

import Uniton.Fring.domain.product.entity.Product;
import Uniton.Fring.domain.product.entity.ProductOption;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Schema(description = "상품 옵션 정보 응답 DTO")
public class ProductOptionResponseDto {

    @Schema(description = "옵션 ID", example = "1")
    private final Long id;

    @Schema(description = "상품 ID", example = "1")
    private final Long productId;

    @Schema(description = "옵션 이름", example = "대용량")
    private final String optionName;

    @Schema(description = "옵션 가격", example = "1500")
    private final BigDecimal additionalPrice;

    @Schema(description = "옵션 사용 가능 여부 (품절)", example = "true")
    private final Boolean available;

    @Builder
    private ProductOptionResponseDto(ProductOption productOption, Product product) {
        this.id = productOption.getId();
        this.productId = productOption.getProductId();
        this.optionName = productOption.getOptionName();
        this.additionalPrice = product.getPrice().multiply(BigDecimal.valueOf(1 - product.getDiscountRate())).add(productOption.getAdditionalPrice());
        this.available = productOption.getAvailable();
    }
}