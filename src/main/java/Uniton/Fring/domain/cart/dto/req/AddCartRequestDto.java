package Uniton.Fring.domain.cart.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "장바구니 추가 요청 DTO")
public class AddCartRequestDto {

    @NotBlank(message = "상품 Id 리스트가 비어있습니다.")
    @Schema(description = "상품 Id 리스트", example = "[1, 2, 3, 4]")
    private List<CartItemDto> items;
}
