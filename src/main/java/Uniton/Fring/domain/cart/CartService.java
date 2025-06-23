package Uniton.Fring.domain.cart;

import Uniton.Fring.domain.cart.dto.req.CartRequestDto;
import Uniton.Fring.domain.cart.dto.res.CartInfoResponseDto;
import Uniton.Fring.domain.cart.dto.res.CartItemResponseDto;
import Uniton.Fring.domain.member.entity.Member;
import Uniton.Fring.domain.member.repository.MemberRepository;
import Uniton.Fring.domain.product.entity.Product;
import Uniton.Fring.domain.product.repository.ProductRepository;
import Uniton.Fring.global.exception.CustomException;
import Uniton.Fring.global.exception.ErrorCode;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;

    @Transactional(readOnly = true)
    public CartInfoResponseDto getCart(UserDetailsImpl userDetails) {

        log.info("[장바구니 조회 요청]");

        Member member = userDetails.getMember();

        // 해당 사용자의 장바구니 항목 조회
        List<Cart> carts = cartRepository.findByMemberId(member.getId());
        if (carts.isEmpty()) {
            log.info("[장바구니 비어 있음]");
            return CartInfoResponseDto.builder().items(Collections.emptyList()).build();
        }

        // 장바구니에 있는 상품 ID만 추출
        List<Long> productIds = carts.stream()
                .map(Cart::getProductId)
                .distinct()
                .toList();

        // 상품 정보 일괄 조회
        List<Product> products = productRepository.findAllById(productIds);
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        // 상품 판매자 일괄 조회
        List<Long> memberIds = products.stream()
                .map(Product::getMemberId)
                .distinct()
                .toList();
        List<Member> sellers = memberRepository.findAllById(memberIds);
        Map<Long, Member> memberMap = sellers.stream()
                .collect(Collectors.toMap(Member::getId, Function.identity()));

        // 장바구니 항목 DTO로 변환
        List<CartItemResponseDto> cartInfoResponseDtos = carts.stream()
                .map(cartItem -> {
                    Product product = productMap.get(cartItem.getProductId());
                    Member seller = memberMap.get(product.getMemberId());

                    return CartItemResponseDto.builder()
                            .cart(cartItem)
                            .memberNickname(seller.getNickname())
                            .product(product)
                            .build();
                })
                .toList();

        log.info("[장바구니 조회 성공]");

        return CartInfoResponseDto.builder().items(cartInfoResponseDtos).build();
    }

    @Transactional
    public CartInfoResponseDto addCart(UserDetailsImpl userDetails, Long productId, CartRequestDto cartRequestDto) {

        log.info("[장바구니 추가 요청]");

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("[농수산 조회 실패] 농수산 없음: productId={}", productId);
                    return new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
                });

        Member member = userDetails.getMember();

        Boolean isAlreadyCart = cartRepository.existsCartByMemberIdAndProductId(member.getId(), productId);

        if (isAlreadyCart) {

        }
        else {

        }

        log.info("[장바구니 추가 성공]");

        return CartInfoResponseDto.builder().build();
    }

    @Transactional
    public CartInfoResponseDto updateCart(UserDetailsImpl userDetails, CartRequestDto cartRequestDto) {

        log.info("[장바구니 수정 요청]");



        log.info("[장바구니 수정 성공]");

        return CartInfoResponseDto.builder().build();
    }

    @Transactional
    public void deleteCart(UserDetailsImpl userDetails) {

        log.info("[장바구니 삭제 요청]");



        log.info("[장바구니 삭제 성공]");
    }
}
