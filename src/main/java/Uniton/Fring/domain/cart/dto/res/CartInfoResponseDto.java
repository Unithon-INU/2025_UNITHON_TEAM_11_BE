package Uniton.Fring.domain.cart.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "장바구니 정보 응답 DTO")
public class CartInfoResponseDto {

    @Schema(description = "판매자 단위 장바구니 그룹 목록", example = "병아리 농장, 고릴라 농장")
    private final List<CartGroupResponseDto> groups;

    @Builder
    private CartInfoResponseDto(List<CartGroupResponseDto> items) {
        this.groups = items;
    }
}
