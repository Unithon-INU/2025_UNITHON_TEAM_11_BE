package Uniton.Fring.domain.purchase.dto.req;

import Uniton.Fring.domain.cart.dto.req.ProductItemRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "구매 요청 DTO")
public class PurchaseRequestDto {

    @NotNull(message = "상품 요소가 비어있습니다.")
    @Schema(description = "구매 상품 요소")
    private List<ProductItemRequestDto> items;
}
