package Uniton.Fring.domain.cart;

import Uniton.Fring.domain.cart.dto.req.ProductItemRequestDto;
import Uniton.Fring.domain.cart.dto.req.CartRequestDto;
import Uniton.Fring.domain.cart.dto.res.CartGroupResponseDto;
import Uniton.Fring.domain.cart.dto.res.CartInfoResponseDto;
import Uniton.Fring.domain.cart.dto.res.ItemResponseDto;
import Uniton.Fring.domain.cart.dto.res.CartUpdateResponseDto;
import Uniton.Fring.domain.member.entity.Member;
import Uniton.Fring.domain.member.repository.MemberRepository;
import Uniton.Fring.domain.product.entity.Product;
import Uniton.Fring.domain.product.entity.ProductOption;
import Uniton.Fring.domain.product.repository.ProductOptionRepository;
import Uniton.Fring.domain.product.repository.ProductRepository;
import Uniton.Fring.global.exception.CustomException;
import Uniton.Fring.global.exception.ErrorCode;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
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
    private final ProductOptionRepository productOptionRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;

    @Transactional(readOnly = true)
    public CartInfoResponseDto getCart(UserDetailsImpl userDetails) {

        log.info("[장바구니 조회 요청] 회원: {}", userDetails.getUsername());

        Member member = userDetails.getMember();

        // 해당 사용자의 장바구니 항목 조회
        List<Cart> carts = cartRepository.findByMemberId(member.getId());
        if (carts.isEmpty()) {
            log.info("[장바구니 비어 있음]");
            return CartInfoResponseDto.builder().items(Collections.emptyList()).build();
        }

        // 장바구니에 있는 상품 ID만 추출
        List<Long> productIds = carts.stream().map(Cart::getProductId).distinct().toList();

        // 상품 정보 일괄 조회
        List<Product> products = productRepository.findAllById(productIds);
        Map<Long, Product> productMap = products.stream().collect(Collectors.toMap(Product::getId, Function.identity()));

        // 상품 판매자 일괄 조회
        List<Long> sellersIds = products.stream().map(Product::getMemberId).distinct().toList();
        List<Member> sellers = memberRepository.findAllById(sellersIds);
        Map<Long, Member> sellerMap = sellers.stream().collect(Collectors.toMap(Member::getId, Function.identity()));

        // 판매자 단위로 그룹핑
        Map<Long, List<Cart>> groupedBySeller = carts.stream()
                .collect(Collectors.groupingBy(cart -> {
                    Product p = productMap.get(cart.getProductId());
                    return p.getMemberId();
                }));

        // 장바구니 정보 응답 생성
        List<CartGroupResponseDto> groups = new ArrayList<>();

        for (Map.Entry<Long, List<Cart>> entry : groupedBySeller.entrySet()) {
            Long sellerId = entry.getKey();
            List<Cart> sellerCarts = entry.getValue();
            Member seller = sellerMap.get(sellerId);

            List<ItemResponseDto> itemResponseDtos = new ArrayList<>();
            BigDecimal totalProductPrice = BigDecimal.ZERO;
            BigDecimal deliveryFee = BigDecimal.ZERO;

            for (Cart cartItem : sellerCarts) {
                Product product = productMap.get(cartItem.getProductId());

                totalProductPrice = totalProductPrice.add(cartItem.getProductPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
                if (deliveryFee.compareTo(BigDecimal.ZERO) == 0) {
                    deliveryFee = product.getDeliveryFee();
                }

                itemResponseDtos.add(ItemResponseDto.builder()
                        .cart(cartItem)
                        .seller(seller)
                        .product(product)
                        .build());
            }

            groups.add(CartGroupResponseDto.builder()
                    .seller(seller)
                    .items(itemResponseDtos)
                    .totalProductPrice(totalProductPrice)
                    .deliveryFee(deliveryFee)
                    .totalPrice(totalProductPrice.add(deliveryFee))
                    .build());
        }

        log.info("[장바구니 조회 성공] 회원: {}", userDetails.getUsername());

        return CartInfoResponseDto.builder().items(groups).build();
    }

    @Transactional
    public ItemResponseDto addCart(UserDetailsImpl userDetails, ProductItemRequestDto productItemRequestDto) {

        log.info("[장바구니 추가 요청] 회원: {}", userDetails.getUsername());

        Product product = productRepository.findById(productItemRequestDto.getProductId())
                .orElseThrow(() -> {
                    log.warn("[농수산 조회 실패] 농수산 없음: productId={}", productItemRequestDto.getProductId());
                    return new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
                });

        Member seller = memberRepository.findById(product.getMemberId())
                .orElseThrow(() -> {
                    log.warn("[유저 정보 조회 실패] 사용자 없음");
                    return new CustomException(ErrorCode.MEMBER_NOT_FOUND);
                });

        Member member = userDetails.getMember();

        Cart cart = cartRepository.findByMemberIdAndProductId(member.getId(), productItemRequestDto.getProductId()).orElse(null);

        if (cart != null) {
            cart.addQuantity(productItemRequestDto.getQuantity());
            log.info("[장바구니 수량 추가] memberNickname={}, productName={}, newQuantity={}", member.getNickname(), product.getName(), cart.getQuantity());
        }
        else {

            ProductOption option = productOptionRepository
                    .findByProductIdAndOptionName(product.getId(), productItemRequestDto.getProductOption())
                    .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_OPTION_NOT_FOUND));

            BigDecimal optionPrice = option.getAdditionalPrice();

            cart = Cart.builder().memberId(member.getId()).product(product)
                    .productOption(productItemRequestDto.getProductOption()).quantity(productItemRequestDto.getQuantity()).optionPrice(optionPrice).build();
            cartRepository.save(cart);
            log.info("[장바구니 항목 추가] memberNickname={}, productName={}", member.getNickname(), product.getName());
        }

        log.info("[장바구니 추가 성공] 회원: {}", userDetails.getUsername());

        return ItemResponseDto.builder().cart(cart).seller(seller).product(product).build();
    }

    @Transactional
    public CartUpdateResponseDto updateCart(UserDetailsImpl userDetails, CartRequestDto cartRequestDto) {

        log.info("[장바구니 수정 요청] 회원: {}", userDetails.getUsername());

        Member member = userDetails.getMember();

        cartRepository.deleteByMemberId(member.getId());
        log.info("[기존 장바구니 항목 삭제 완료] memberNickname={}", member.getNickname());

        List<ItemResponseDto> savedItems = new ArrayList<>();

        for (ProductItemRequestDto cartItem : cartRequestDto.getItems()) {

            // 상품 조회
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> {
                        log.warn("[상품 조회 실패] productId={}", cartItem.getProductId());
                        return new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
                    });

            // 판매자 조회
            Member seller = memberRepository.findById(product.getMemberId())
                    .orElseThrow(() -> {
                        log.warn("[판매자 조회 실패] memberId={}", product.getMemberId());
                        return new CustomException(ErrorCode.MEMBER_NOT_FOUND);
                    });

            // 옵션 조회
            ProductOption option = productOptionRepository.findByProductIdAndOptionName(product.getId(), cartItem.getProductOption())
                    .orElseThrow(() -> {
                        log.warn("[상품 옵션 조회 실패] productId={}", product.getId());
                        return new CustomException(ErrorCode.PRODUCT_OPTION_NOT_FOUND);
                    });

            BigDecimal optionPrice = option.getAdditionalPrice();

            // 장바구니 객체 생성 및 저장
            Cart cart = Cart.builder()
                    .memberId(member.getId())
                    .product(product)
                    .productOption(cartItem.getProductOption())
                    .quantity(cartItem.getQuantity())
                    .optionPrice(optionPrice)
                    .build();

            cartRepository.save(cart);

            savedItems.add(ItemResponseDto.builder().cart(cart).seller(seller).product(product).build());
        }

        log.info("[장바구니 수정 성공] 회원: {}", userDetails.getUsername());

        return CartUpdateResponseDto.builder().items(savedItems).build();
    }

    @Transactional
    public void deleteCart(UserDetailsImpl userDetails) {

        log.info("[장바구니 삭제 요청]");

        Member member = userDetails.getMember();

        List<Cart> carts = cartRepository.findByMemberId(member.getId());
        if (carts.isEmpty()) {
            log.warn("[장바구니 삭제 요청 실패] 이미 비어 있음: memberNickname={}", member.getNickname());
            return;
        }

        cartRepository.deleteAll(carts);

        log.info("[장바구니 삭제 성공]");
    }
}
