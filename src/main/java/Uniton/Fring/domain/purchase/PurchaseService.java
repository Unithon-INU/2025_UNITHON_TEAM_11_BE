package Uniton.Fring.domain.purchase;

import Uniton.Fring.domain.purchase.dto.req.PurchaseRequestDto;
import Uniton.Fring.domain.purchase.dto.res.PurchaseResponseDto;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

    @Transactional
    public PurchaseResponseDto purchase(UserDetailsImpl userDetails, PurchaseRequestDto purchaseRequestDto) {

        log.info("[상품 주문 요청] 회원: {}", userDetails.getUsername());

        PurchaseResponseDto purchaseResponseDto = PurchaseResponseDto.builder().build();

        log.info("[상품 주문 성공]");

        return purchaseResponseDto;
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
