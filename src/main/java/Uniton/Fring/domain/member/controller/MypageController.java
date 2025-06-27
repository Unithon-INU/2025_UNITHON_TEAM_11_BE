package Uniton.Fring.domain.member.controller;

import Uniton.Fring.domain.farmer.dto.res.StoreResponseDto;
import Uniton.Fring.domain.member.api.MypageApiSpecification;
import Uniton.Fring.domain.member.dto.req.ApplyFarmerRequestDto;
import Uniton.Fring.domain.member.dto.req.MypageRequestDto;
import Uniton.Fring.domain.member.dto.res.MypageDetailResponseDto;
import Uniton.Fring.domain.member.dto.res.MypageResponseDto;
import Uniton.Fring.domain.member.dto.res.MypageReviewResponseDto;
import Uniton.Fring.domain.member.service.MypageService;
import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.domain.purchase.dto.res.SimplePurchaseResponseDto;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MypageController implements MypageApiSpecification {

    private final MypageService mypageService;

    // 마이페이지 조회
    @GetMapping
    public ResponseEntity<MypageResponseDto> getMypage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(mypageService.getMypage(userDetails));
    }

    // 마이페이지 상세 조회
    @GetMapping("/detail")
    public ResponseEntity<MypageDetailResponseDto> getDetailMypage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                   @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.status(HttpStatus.OK).body(mypageService.getDetailMypage(userDetails, page));
    }

    // 마이페이지 수정
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MypageResponseDto> updateMypage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                          @RequestPart @Valid MypageRequestDto mypageRequestDto,
                                                          @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.status(HttpStatus.OK).body(mypageService.updateMypage(userDetails, mypageRequestDto, image));
    }

    // 주문 내역
    @GetMapping("/orders")
    public ResponseEntity<List<SimplePurchaseResponseDto>> getPurchaseHistory(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                           @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.status(HttpStatus.OK).body(mypageService.getPurchaseHistory(userDetails, page));
    }

    // 최근 본 상품
    @GetMapping("/recent/products")
    public ResponseEntity<List<SimpleProductResponseDto>> getRecentViewedProducts(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                                  @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.status(HttpStatus.OK).body(mypageService.getRecentViewedProducts(userDetails, page));
    }

    // 전체 리뷰 내역
    @GetMapping("/reviews")
    public ResponseEntity<MypageReviewResponseDto> getMyReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                       @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(mypageService.getMyReview(userDetails, page));
    }

//    // 문의 내역
//    @GetMapping("/inquiries")
//    public ResponseEntity<MypageResponseDto> getInquiryHistory(@AuthenticationPrincipal UserDetailsImpl userDetails,
//                                                               @RequestParam(defaultValue = "0") int page) {
//        return ResponseEntity.ok(mypageService.getInquiryHistory(userDetails, page));
//    }

    // 입점 신청
    @PostMapping(value = "/applyFarmer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StoreResponseDto> applyFarmer(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                        @RequestPart @Valid ApplyFarmerRequestDto applyFarmerRequestDto,
                                                        @RequestPart(value = "RegistFile", required = false) MultipartFile registFile,
                                                        @RequestPart(value = "Passbook", required = false) MultipartFile passbook,
                                                        @RequestPart(value = "certifidoc", required = false) MultipartFile certifidoc,
                                                        @RequestPart(value = "profile", required = false) MultipartFile profile) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mypageService.applyFarmer(userDetails, applyFarmerRequestDto, registFile, passbook, certifidoc, profile));
    }

    // 사업자 등록번호 or 농가확인번호 중복 확인
    @GetMapping("/registNum/{registNum}")
    public ResponseEntity<Boolean> checkRegistNumDuplicated(@PathVariable String registNum) {
        return ResponseEntity.status(HttpStatus.OK).body(mypageService.checkRegisNumDuplicated(registNum));
    }
}
