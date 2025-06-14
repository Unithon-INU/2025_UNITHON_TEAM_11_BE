package Uniton.Fring.domain.cart.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "장바구니 추가 상품 요소 DTO")
public class CartItemDto {

    @NotBlank(message = "상품 Id가 비어있습니다.")
    @Schema(description = "상품 Id", example = "1")
    private Long productId;

    @NotBlank(message = "상품 수량이 비어있습니다.")
    @Schema(description = "상품 수량", example = "2")
    private int quantity;
}
