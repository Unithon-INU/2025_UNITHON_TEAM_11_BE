package Uniton.Fring.domain.cart.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "장바구니 응답 DTO")
public class AddCartResponseDto {

//    @Schema(description = "장바구니를 추가한 회원의 닉네임", example = "johndoe")
//    private final String memberNickname;
//
//    @Schema(description = "장바구니에 담긴 상품 이름 목록", example = "[\"포도\", \"사과\"]")
//    private final List<String> productName;
//
//    @Builder
//    private AddCartResponseDto(List<Cart> carts) {
//        this.memberNickname = carts.get(0).getMember().getNickname();
//        this.productName = carts.stream().map(cart -> cart.getProduct().getName()).collect(Collectors.toList());
//    }
}
