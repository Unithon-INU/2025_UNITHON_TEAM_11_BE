package Uniton.Fring.domain.member.controller;

import Uniton.Fring.domain.member.dto.req.DeleteMemberRequestDto;
import Uniton.Fring.domain.member.dto.req.LoginRequestDto;
import Uniton.Fring.domain.member.dto.req.SignupRequestDto;
import Uniton.Fring.domain.member.dto.res.LoginResponseDto;
import Uniton.Fring.domain.member.dto.res.SignupResponseDto;
import Uniton.Fring.domain.member.service.MemberService;
import Uniton.Fring.global.security.jwt.JwtTokenRequestDto;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController implements MemberApiSpecification {

    private final MemberService memberService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.signup(signupRequestDto));
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

    // 닉네임 중복 확인
    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<Boolean> checkNicknameDuplicated(@PathVariable String nickname) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.checkNicknameDuplicated(nickname));
    }

    // 회원탈퇴
    @DeleteMapping
    public ResponseEntity<Boolean> deleteMember(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody DeleteMemberRequestDto deleteMemberRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.deleteMember(userDetails.getMember(),deleteMemberRequestDto));
    }

    // ROLE 농부로 변경
    @PutMapping
    public  ResponseEntity<Boolean> changeToFarmer(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.changeToFarmer(userDetails.getMember()));
    }
}