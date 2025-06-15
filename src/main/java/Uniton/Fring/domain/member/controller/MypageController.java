package Uniton.Fring.domain.member.controller;

import Uniton.Fring.domain.member.api.MypageApiSpecification;
import Uniton.Fring.domain.member.service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MypageController implements MypageApiSpecification {

    private final MypageService mypageService;

//    // 마이페이지 조회 (소비자)
//    @GetMapping("/mypage")
//    public ResponseEntity<MypageResponseDto> getMypage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(mypageService.getMypage(userDetails));
//    }

    // 마이페이지 조회 (판매자)

    // 입점 신청

    // 마이페이지 수정

    // 댓글 내역

    // 문의 내역

    // 레시피 리뷰 내역

    // 상품 리뷰 내역

    // 고객센터
}
