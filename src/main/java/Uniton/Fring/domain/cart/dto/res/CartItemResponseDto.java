package Uniton.Fring.domain.cart.dto.res;

import Uniton.Fring.domain.cart.Cart;
import Uniton.Fring.domain.member.entity.Member;
import Uniton.Fring.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@Schema(description = "장바구니 상품 요소 응답 DTO")
public class CartItemResponseDto {

    @Schema(description = "장바구니 Id", example = "1")
    private Long cartId;

    @Schema(description = "상품 Id", example = "1")
    private Long productId;

    @Schema(description = "판매자 Id", example = "1")
    private Long sellerId;

    @Schema(description = "상품 이름", example = "토마토")
    private String productName;

    @Schema(description = "상품 이미지", example = "https://fring-s3.s3.ap-northeast-2.amazonaws.com/carts/image.jpg")
    private String imageUrl;

    @Schema(description = "상품 수량", example = "2")
    private int quantity;

    @Schema(description = "상품 옵션", example = "단품 계란 15구, 1판")
    private String productOption;

    @Schema(description = "상품 가격", example = "6090")
    private BigDecimal productPrice;

    @Builder
    private CartItemResponseDto(Cart cart, Member seller, Product product) {
        this.cartId = cart.getId();
        this.productId = product.getId();
        this.sellerId = seller.getId();
        this.productName = product.getName();
        this.imageUrl = product.getMainImageUrl();
        this.quantity = cart.getQuantity();
        this.productOption = cart.getProductOption();
        this.productPrice = cart.getProductPrice();
    }
}
