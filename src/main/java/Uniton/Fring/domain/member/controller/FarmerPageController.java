package Uniton.Fring.domain.member.controller;

import Uniton.Fring.domain.member.dto.res.StoreItemsResponseDto;
import Uniton.Fring.domain.member.service.FarmerPageService;
import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class FarmerPageController {

    private final FarmerPageService farmerPageService;

    // 스토어 관리
    @GetMapping
    public ResponseEntity<StoreItemsResponseDto<SimpleProductResponseDto>> getStore(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                                    @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.status(HttpStatus.OK).body(farmerPageService.getStore(userDetails, page));
    }

    // 주문 / 배송 관리

    // 취소 / 반품 관리

    // 받은 문의내역

}
