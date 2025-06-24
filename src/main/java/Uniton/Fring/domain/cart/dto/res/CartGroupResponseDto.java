package Uniton.Fring.domain.cart.dto.res;

import Uniton.Fring.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Schema(description = "판매자(농장) 단위 장바구니 그룹 응답 DTO")
public class CartGroupResponseDto {

    @Schema(description = "판매자 ID")
    private final Long sellerId;

    @Schema(description = "판매자 닉네임")
    private final String sellerNickname;

    @Schema(description = "판매자별 장바구니 상품 목록")
    private final List<CartItemResponseDto> items;

    @Schema(description = "판매자별 상품 총합", example = "6000")
    private final BigDecimal totalProductPrice;

    @Schema(description = "판매자별 배송비", example = "3000")
    private final BigDecimal deliveryFee;

    @Schema(description = "판매자별 결제 총액", example = "9000")
    private final BigDecimal totalPrice;

    @Builder
    public CartGroupResponseDto(Member seller, List<CartItemResponseDto> items,
                                BigDecimal totalProductPrice, BigDecimal deliveryFee, BigDecimal totalPrice) {
        this.sellerId = seller.getId();
        this.sellerNickname = seller.getNickname();
        this.items = items;
        this.totalProductPrice = totalProductPrice;
        this.deliveryFee = deliveryFee;
        this.totalPrice = totalPrice;
    }
}