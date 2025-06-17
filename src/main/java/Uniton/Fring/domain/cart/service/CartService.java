package Uniton.Fring.domain.cart.service;

import Uniton.Fring.domain.cart.repository.CartRepository;
import Uniton.Fring.domain.product.repository.ProductRepository;
import Uniton.Fring.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;

//    public AddCartResponseDto getCartItems(UserDetailsImpl userDetails) {
//        List<Cart> carts = cartRepository.findAllByMemberId(userDetails.getMember().getId());
//
//        List;
//
//        return CartInfoResponseDto.builder().build();
//    }

//    @Transactional
//    public AddCartResponseDto addCartItems(UserDetailsImpl userDetails, List<CartItemDto> items) {
//
//        log.info("[장바구니 추가 요청] memberId={}, product={}", userDetails.getMember().getId(), items);
//
//        Member member = memberRepository.findById(userDetails.getMember().getId())
//                .orElseThrow(() -> {
//                    log.warn("[장바구니 추가 실패] 사용자 없음: memberId={}", userDetails.getMember().getId());
//                    return new CustomException(ErrorCode.MEMBER_NOT_FOUND);
//                });
//
//        List<Long> productIds = items.stream().map(CartItemDto::getProductId).toList();
//
//        List<FarmProduct> farmProducts = farmProductRepository.findAllById(productIds);
//
//        Map<Long, FarmProduct> productMap = farmProducts.stream()
//                .collect(Collectors.toMap(FarmProduct::getId, farmProduct -> farmProduct));
//
//        List<Cart> carts = new ArrayList<>();
//
//        for (CartItemDto item : items) {
//            FarmProduct farmProduct = productMap.get(item.getProductId());
//            if (farmProduct == null) {
//                log.warn("[장바구니 추가 실패] 존재하지 않는 상품 ID= {}", item.getProductId());
//                throw new CustomException(ErrorCode.Product_NOT_FOUND);
//            }
//
//            Cart cart = Cart.builder()
//                    .member(member)
//                    .product(farmProduct)
//                    .quantity(item.getQuantity())
//                    .build();
//
//            member.addCart(cart);
//            carts.add(cart);
//        }
//
//        log.info("[장바구니 추가 완료] memberId={}", member.getId());
//
//        return AddCartResponseDto.builder().carts(carts).build();
//    }
}
