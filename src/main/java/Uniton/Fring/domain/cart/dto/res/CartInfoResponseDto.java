package Uniton.Fring.domain.cart.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "장바구니 정보 응답 DTO")
public class CartInfoResponseDto {

    @Schema(description = "장바구니 상품 요소", example = "계란, 토마토")
    private final List<CartItemResponseDto> items;

    @Builder
    private CartInfoResponseDto(List<CartItemResponseDto> items) {
        this.items = items;
    }
}
