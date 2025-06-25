package Uniton.Fring.domain.purchase.controller;

import Uniton.Fring.domain.purchase.PurchaseService;
import Uniton.Fring.domain.purchase.dto.req.PurchaseRequestDto;
import Uniton.Fring.domain.purchase.dto.res.PurchaseResponseDto;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/purchases")
public class PurchaseController implements PurchaseApiSpecification{

    private final PurchaseService purchaseService;

    // 상품 구매
    @PostMapping
    public ResponseEntity<PurchaseResponseDto> purchase(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                       @Valid @RequestBody PurchaseRequestDto purchaseRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseService.purchase(userDetails, purchaseRequestDto));
    }

//    // 구매 취소
//    @PutMapping("/{purchaseId}")
//    public ResponseEntity<CartInfoResponseDto> cancelPurchase(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long purchaseId,
//                                                              @Valid @RequestBody CancelPurchaseRequestDto cancelPurchaseRequestDto) {
//        return ResponseEntity.status(HttpStatus.OK).body(purchaseService.cancelPurchase(userDetails, purchaseId, cancelPurchaseRequestDto));
//    }
}
