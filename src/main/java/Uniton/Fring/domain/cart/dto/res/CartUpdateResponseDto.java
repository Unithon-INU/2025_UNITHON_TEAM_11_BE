package Uniton.Fring.domain.cart.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "장바구니 수정 정보 응답 DTO")
public class CartUpdateResponseDto {

    @Schema(description = "판매자 단위 장바구니 그룹 목록", example = "병아리 농장, 고릴라 농장")
    private final List<CartItemResponseDto> items;

    @Builder
    private CartUpdateResponseDto(List<CartItemResponseDto> items) {
        this.items = items;
    }
}
