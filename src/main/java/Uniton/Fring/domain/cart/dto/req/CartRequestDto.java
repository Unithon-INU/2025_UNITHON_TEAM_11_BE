package Uniton.Fring.domain.cart.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "장바구니 추가 요청 DTO")
public class CartRequestDto {

    @NotNull(message = "장바구니 상품 요소가 비어있습니다.")
    @Schema(description = "장바구니 상품 요소")
    private List<CartItemRequestDto> items;
}
