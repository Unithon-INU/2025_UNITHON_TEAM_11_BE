package Uniton.Fring.domain.cart.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@Schema(description = "구매 / 장바구니 상품 요소 요청 DTO")
public class ProductItemRequestDto {

    @NotNull(message = "상품 Id가 비어있습니다.")
    @Schema(description = "상품 Id", example = "1")
    private Long productId;

    @Schema(description = "상품 옵션", example = "단품 계란 15구, 1판")
    private String productOption;

    @NotNull(message = "상품 수량이 비어있습니다.")
    @Schema(description = "상품 수량", example = "2")
    private int quantity;

    @Schema(description = "상품 가격", example = "6000")
    private BigDecimal productPrice;
}
