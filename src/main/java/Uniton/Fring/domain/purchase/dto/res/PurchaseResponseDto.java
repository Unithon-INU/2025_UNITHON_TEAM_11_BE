package Uniton.Fring.domain.purchase.dto.res;

import Uniton.Fring.domain.delivery.Delivery;
import Uniton.Fring.domain.purchase.entity.Purchase;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Schema(description = "구매 상세 정보 응답 DTO")
public class PurchaseResponseDto {

    @Schema(description = "주문 ID", example = "1")
    private final Long id;

    @Schema(description = "상품 목록", example = "병아리 농장, 고릴라 농장")
    private final List<SimplePurchaseResponseDto> simplePurchaseResponseDtos;

    @Schema(description = "결제 방법", example = "프링 페이")
    private final String paymentMethod;

    @Schema(description = "총 상품 금액", example = "6000")
    private final BigDecimal productPrice;

    @Schema(description = "배송비", example = "3000")
    private final BigDecimal deliveryFee;

    @Schema(description = "총 가격", example = "9000")
    private final BigDecimal totalPrice;

    @Schema(description = "구매자 닉네임", example = "김프링")
    private final String consumerNickname;

    @Schema(description = "배송지", example = "경기도 프링시 프링구 프링동 프링마 123-45, 607호")
    private final String consumerAddress;

    @Schema(description = "구매자 전화번호", example = "010-1234-5678")
    private final String consumerPhoneNumber;

    @Builder
    public PurchaseResponseDto(Purchase purchase, Delivery delivery, List<SimplePurchaseResponseDto> simplePurchaseResponseDtos) {
        this.id = purchase.getId();
        this.simplePurchaseResponseDtos = simplePurchaseResponseDtos;
        this.paymentMethod = purchase.getPayMethod();
        this.productPrice = purchase.getProductPrice();
        this.deliveryFee = purchase.getDeliveryFee();
        this.totalPrice = purchase.getTotalPrice();
        this.consumerNickname = delivery.getName();
        this.consumerAddress = delivery.getAddress() + delivery.getAddressDetail();
        this.consumerPhoneNumber = delivery.getPhoneNumber();
    }
}
