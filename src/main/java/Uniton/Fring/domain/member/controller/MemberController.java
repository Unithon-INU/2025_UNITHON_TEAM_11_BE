package Uniton.Fring.domain.member.controller;

import Uniton.Fring.domain.member.api.MemberApiSpecification;
import Uniton.Fring.domain.member.dto.req.DeleteMemberRequestDto;
import Uniton.Fring.domain.member.dto.req.LoginRequestDto;
import Uniton.Fring.domain.member.dto.req.SignupRequestDto;
import Uniton.Fring.domain.member.dto.res.*;
import Uniton.Fring.domain.member.service.MemberService;
import Uniton.Fring.global.security.jwt.JwtTokenRequestDto;
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
@RequestMapping("/api/members")
public class MemberController implements MemberApiSpecification {

    private final MemberService memberService;

    // 회원가입
    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SignupResponseDto> signup(@RequestPart @Valid SignupRequestDto signupRequestDto,
                                                    @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.signup(signupRequestDto, image));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.login(loginRequestDto));
    }

    // 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(@Valid @RequestBody JwtTokenRequestDto jwtTokenRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.refresh(jwtTokenRequestDto));
    }

    // 이메일 중복 확인
    @GetMapping("/email/{email}")
    public ResponseEntity<Boolean> checkEmailDuplicated(@PathVariable String email) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.checkEmailDuplicated(email));
    }

    // 아이디 중복 확인
    @GetMapping("/username/{username}")
    public ResponseEntity<Boolean> checkUsernameDuplicated(@PathVariable String username) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.checkUsernameDuplicated(username));
    }

    // 닉네임 중복 확인
    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<Boolean> checkNicknameDuplicated(@PathVariable String nickname) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.checkNicknameDuplicated(nickname));
    }

    // 회원탈퇴
    @DeleteMapping
    public ResponseEntity<Void> deleteMember(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody DeleteMemberRequestDto deleteMemberRequestDto) {
        memberService.deleteMember(userDetails.getMember(),deleteMemberRequestDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // ROLE 농부로 변경
    @PutMapping("/role")
    public ResponseEntity<Void> changeToFarmer(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        memberService.changeToFarmer(userDetails.getMember());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 유저 검색
    @GetMapping("/search")
    public ResponseEntity<List<SearchMemberResponseDto>> searchMember(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.searchMember(keyword, page));
    }

    // 유저 랭킹
    @GetMapping("/ranking")
    public ResponseEntity<List<MemberRankingResponseDto>> getRankingRecipeMember() {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.getRankingRecipeMember());
    }

    // 유저 정보 조회
    @GetMapping("{memberId}")
    public ResponseEntity<MemberInfoResponseDto> getMemberInfo(@PathVariable Long memberId) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.getMemberInfo(memberId));
    }
}