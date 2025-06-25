package Uniton.Fring.domain.purchase.service;

import Uniton.Fring.domain.cart.dto.req.ProductItemRequestDto;
import Uniton.Fring.domain.delivery.Delivery;
import Uniton.Fring.domain.delivery.DeliveryService;
import Uniton.Fring.domain.delivery.dto.req.DeliveryRequestDto;
import Uniton.Fring.domain.member.entity.Member;
import Uniton.Fring.domain.member.repository.MemberRepository;
import Uniton.Fring.domain.product.entity.Product;
import Uniton.Fring.domain.product.entity.ProductOption;
import Uniton.Fring.domain.product.repository.ProductOptionRepository;
import Uniton.Fring.domain.product.repository.ProductRepository;
import Uniton.Fring.domain.purchase.dto.req.PurchaseRequestDto;
import Uniton.Fring.domain.purchase.dto.res.PurchaseResponseDto;
import Uniton.Fring.domain.purchase.dto.res.SimplePurchaseResponseDto;
import Uniton.Fring.domain.purchase.entity.Purchase;
import Uniton.Fring.domain.purchase.entity.PurchaseItem;
import Uniton.Fring.domain.purchase.repository.PurchaseItemRepository;
import Uniton.Fring.domain.purchase.repository.PurchaseRepository;
import Uniton.Fring.global.exception.CustomException;
import Uniton.Fring.global.exception.ErrorCode;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final PurchaseItemRepository purchaseItemRepository;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final MemberRepository memberRepository;
    private final JdbcTemplate jdbc;
    private final DeliveryService deliveryService;

    @Transactional
    public PurchaseResponseDto purchase(UserDetailsImpl userDetails, PurchaseRequestDto purchaseRequestDto) {

        log.info("[상품 주문 요청] 회원: {}", userDetails.getUsername());

        Member buyer = userDetails.getMember();

        List<SimplePurchaseResponseDto> simplePurchaseResponseDtos = new ArrayList<>();
        List<ProductItemRequestDto> requestItems = purchaseRequestDto.getItems();

        // 배송 정보 저장
        DeliveryRequestDto deliveryDto = purchaseRequestDto.getDeliveryRequestDto();
        Delivery delivery = deliveryService.saveOrGet(deliveryDto, buyer.getId());

        // 주문 번호 생성
        int seq = nextSeqToday();
        String orderNo = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                + "-" + String.format("%06d", seq);

        // Purchase 저장
        Purchase purchase = purchaseRepository.save(Purchase.builder()
                .memberId(buyer.getId())
                .deliveryId(delivery.getId())
                .purchaseNumber(orderNo)
                .productPrice(purchaseRequestDto.getProductPrice())
                .deliveryFee(purchaseRequestDto.getDeliveryFee())
                .totalPrice(purchaseRequestDto.getTotalPrice())
                .payMethod(purchaseRequestDto.getPaymentMethod())
                .build()
        );

        // PurchaseItem 저장 & 응답 DTO 구성
        for (ProductItemRequestDto itemRequestDto : requestItems) {
            Product product = productRepository.findById(itemRequestDto.getProductId())
                    .orElseThrow(() -> {
                        log.warn("[상품 조회 실패] productId={}", itemRequestDto.getProductId());
                        return new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
                    });

            ProductOption productOption = productOptionRepository.findByProductIdAndOptionName(product.getId(), itemRequestDto.getProductOption())
                    .orElseThrow(() -> {
                        log.warn("[상품 옵션 조회 실패] productId={}", product.getId());
                        return new CustomException(ErrorCode.PRODUCT_OPTION_NOT_FOUND);
                    });

            Member seller = memberRepository.findById(product.getMemberId())
                    .orElseThrow(() -> {
                        log.warn("[판매자 조회 실패] memberId={}", product.getMemberId());
                        return new CustomException(ErrorCode.MEMBER_NOT_FOUND);
                    });

            // 실제 주문 항목 저장
            PurchaseItem item = purchaseItemRepository.save(
                    PurchaseItem.builder()
                            .purchaseId(purchase.getId())
                            .productId(product.getId())
                            .quantity(itemRequestDto.getQuantity())
                            .productOption(productOption.getOptionName())
                            .productPrice(itemRequestDto.getProductPrice())
                            .build()
            );

            simplePurchaseResponseDtos.add(SimplePurchaseResponseDto.builder()
                    .purchaseItem(item)
                    .purchase(purchase)
                    .sellerNickname(seller.getNickname())
                    .status(purchase.getStatus().getDescription())
                    .product(product)
                    .build());
        }

        log.info("[상품 주문 성공] purchaseId={}", purchase.getId());

        return PurchaseResponseDto.builder()
                .purchase(purchase)
                .delivery(delivery)
                .simplePurchaseResponseDtos(simplePurchaseResponseDtos)
                .build();
    }

    @Transactional
    public int nextSeqToday() {
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

        Integer seq = jdbc.queryForObject(
                "SELECT seq_no FROM purchase_seq WHERE seq_date = ? FOR UPDATE",
                Integer.class, today);

        if (seq == null) {
            jdbc.update("INSERT INTO purchase_seq (seq_date, seq_no) VALUES (?, 1)", today);
            return 1;
        } else {
            jdbc.update("UPDATE purchase_seq SET seq_no = seq_no + 1 WHERE seq_date = ?", today);
            return jdbc.queryForObject("SELECT seq_no FROM purchase_seq WHERE seq_date = ?", Integer.class, today);
        }
    }

//    @Transactional
//    public void cancelPurchase(UserDetailsImpl userDetails, Long purchaseId) {
//
//        log.info("[주문 취소 요청]");
//
//        Member member = userDetails.getMember();
//
//        Purchase purchase = purchaseRepository.findById(purchaseId)
//                .orElseThrow(() -> {
//                    log.warn("[주문 취소 실패] 주문 내역을 찾을 수 없습니다. purchaseId: {}", purchaseId);
//                    return new CustomException(ErrorCode.PURCHASE_NOT_FOUND);
//                });
//
//        if (!purchase.getMemberId().equals(member.getId())) {
//            log.warn("[주문 취소 실패] 사용자 권한 없음: memberId={}, purchaseOwnerId={}", member.getId(), purchase.getMemberId());
//            throw new CustomException(ErrorCode.PURCHASE_MEMBER_NOT_MATCH);
//        }
//
//        cartRepository.deleteAll(carts);
//
//        log.info("[주문 취소 성공]");
//    }
}
