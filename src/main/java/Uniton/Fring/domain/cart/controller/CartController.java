package Uniton.Fring.domain.cart.controller;

import Uniton.Fring.domain.cart.CartService;
import Uniton.Fring.domain.cart.dto.req.ProductItemRequestDto;
import Uniton.Fring.domain.cart.dto.req.CartRequestDto;
import Uniton.Fring.domain.cart.dto.res.CartInfoResponseDto;
import Uniton.Fring.domain.cart.dto.res.CartItemResponseDto;
import Uniton.Fring.domain.cart.dto.res.CartUpdateResponseDto;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController implements CartApiSpecification {

    private final CartService cartService;

    // 장바구니 조회
    @GetMapping
    public ResponseEntity<CartInfoResponseDto> getCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(cartService.getCart(userDetails));
    }

    // 장바구니 추가
    @PostMapping
    public ResponseEntity<CartItemResponseDto> addCart(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                       @RequestBody ProductItemRequestDto productItemRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addCart(userDetails, productItemRequestDto));
    }

    // 장바구니 수정
    @PutMapping
    public ResponseEntity<CartUpdateResponseDto> updateCart(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                            @RequestBody CartRequestDto cartRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(cartService.updateCart(userDetails, cartRequestDto));
    }

    // 장바구니 삭제
    @DeleteMapping
    public ResponseEntity<CartInfoResponseDto> deleteCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        cartService.deleteCart(userDetails);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
