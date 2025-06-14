package Uniton.Fring.domain.cart.controller;

import Uniton.Fring.domain.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

//    // 장바구니 조회
//    @GetMapping
//    public ResponseEntity<AddCartResponseDto> getCartItems(@AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return ResponseEntity.status(HttpStatus.OK).body(cartService.getCartItems(userDetails));
//    }

//    // 장바구니 추가
//    @PostMapping
//    public ResponseEntity<AddCartResponseDto> addCartItems(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody AddCartRequestDto addCartRequestDto) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addCartItems(userDetails, addCartRequestDto.getItems()));
//    }

//    // 장바구니 수정
//    @PutMapping
//    public ResponseEntity<AddCartResponseDto> updateCartItems(@AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return ResponseEntity.status(HttpStatus.OK).body(cartService.updateCartItems(userDetails));
//    }
//
//    // 장바구니 삭제
//    @DeleteMapping
//    public ResponseEntity<AddCartResponseDto> deleteCartItems(@AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return ResponseEntity.status(HttpStatus.OK).body(cartService.deleteCartItems(userDetails));
//    }
}
