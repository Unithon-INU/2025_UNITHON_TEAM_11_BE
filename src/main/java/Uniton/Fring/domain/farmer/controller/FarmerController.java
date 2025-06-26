package Uniton.Fring.domain.farmer.controller;

import Uniton.Fring.domain.farmer.FarmerService;
import Uniton.Fring.domain.farmer.dto.req.UpdateStoreRequestDto;
import Uniton.Fring.domain.farmer.dto.res.StoreItemsResponseDto;
import Uniton.Fring.domain.farmer.dto.res.StoreResponseDto;
import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class FarmerController implements FarmerApiSpecification {

    private final FarmerService farmerService;

    // 스토어 관리
    @GetMapping
    public ResponseEntity<StoreItemsResponseDto<SimpleProductResponseDto>> getStore(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                                    @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.status(HttpStatus.OK).body(farmerService.getStore(userDetails, page));
    }

    // 프로필 수정
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StoreResponseDto> updateStore(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                        @RequestPart @Valid UpdateStoreRequestDto updateStoreRequestDto,
                                                        @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.status(HttpStatus.OK).body(farmerService.updateStore(userDetails, updateStoreRequestDto, image));
    }

    // 주문 / 배송 관리

    // 취소 / 반품 관리

    // 받은 문의내역
}
