package Uniton.Fring.domain.member.controller;

import Uniton.Fring.domain.member.api.MemberApiSpecification;
import Uniton.Fring.domain.member.dto.req.DeleteMemberRequestDto;
import Uniton.Fring.domain.member.dto.res.MemberInfoResponseDto;
import Uniton.Fring.domain.member.dto.res.SimpleMemberResponseDto;
import Uniton.Fring.domain.member.service.MemberService;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController implements MemberApiSpecification {

    private final MemberService memberService;

    // 유저 검색
    @GetMapping("/search")
    public ResponseEntity<List<SimpleMemberResponseDto>> searchRecipeMember(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.searchRecipeMember(userDetails, keyword, page));
    }

    // 판매자 유저 검색
    @GetMapping("/search/farmer")
    public ResponseEntity<List<SimpleMemberResponseDto>> searchFarmerMember(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.searchFarmerMember(userDetails, keyword, page));
    }

    // 레시피 유저 랭킹
    @GetMapping("/ranking")
    public ResponseEntity<List<SimpleMemberResponseDto>> getRankingRecipeMember(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.getRankingRecipeMember(userDetails));
    }

    // 판매자 유저 랭킹
    @GetMapping("/ranking/product")
    public ResponseEntity<List<SimpleMemberResponseDto>> getRankingFarmer(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.getRankingFarmer(userDetails));
    }

    // 유저 정보 조회
    @GetMapping("{memberId}")
    public ResponseEntity<MemberInfoResponseDto> getMemberInfo(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                               @PathVariable Long memberId,
                                                               @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.getMemberInfo(userDetails, memberId, page));
    }

    // 회원탈퇴
    @DeleteMapping
    public ResponseEntity<Void> deleteMember(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody DeleteMemberRequestDto deleteMemberRequestDto) {
        memberService.deleteMember(userDetails.getMember(),deleteMemberRequestDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}