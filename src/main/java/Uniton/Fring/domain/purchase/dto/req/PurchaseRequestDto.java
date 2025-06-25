package Uniton.Fring.domain.purchase.dto.req;

import Uniton.Fring.domain.cart.dto.req.ProductItemRequestDto;
import Uniton.Fring.domain.delivery.dto.req.DeliveryRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "구매 요청 DTO")
public class PurchaseRequestDto {

    @NotNull(message = "상품 요소가 비어있습니다.")
    @Schema(description = "구매 상품 요소")
    private List<ProductItemRequestDto> items;

    @NotNull(message = "배송지가 비어있습니다.")
    @Schema(description = "배송지")
    private DeliveryRequestDto deliveryRequestDto;

    @NotBlank(message = "결제 수단이 비어있습니다.")
    @Schema(description = "결제 수단")
    private String paymentMethod;

    @NotNull(message = "상품 금액이 비어있습니다.")
    @Schema(description = "상품 금액")
    private BigDecimal productPrice;

    @NotNull(message = "배송비가 비어있습니다.")
    @Schema(description = "배송비")
    private BigDecimal deliveryFee;

    @NotNull(message = "결제 금액이 비어있습니다.")
    @Schema(description = "결제 금액")
    private BigDecimal totalPrice;
}
