package Uniton.Fring.domain.member.controller;

import Uniton.Fring.domain.member.api.AuthApiSpecification;
import Uniton.Fring.domain.member.dto.req.LoginRequestDto;
import Uniton.Fring.domain.member.dto.req.SignupRequestDto;
import Uniton.Fring.domain.member.dto.res.LoginResponseDto;
import Uniton.Fring.domain.member.dto.res.SignupResponseDto;
import Uniton.Fring.domain.member.service.AuthService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController implements AuthApiSpecification {

    private final AuthService authService;

    // 회원가입
    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SignupResponseDto> signup(@RequestPart @Valid SignupRequestDto signupRequestDto,
                                                    @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(signupRequestDto, image));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(loginRequestDto));
    }

    // 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(@Valid @RequestBody JwtTokenRequestDto jwtTokenRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.refresh(jwtTokenRequestDto));
    }

    // 이메일 중복 확인
    @GetMapping("/email/{email}")
    public ResponseEntity<Boolean> checkEmailDuplicated(@PathVariable String email) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.checkEmailDuplicated(email));
    }

    // 아이디 중복 확인
    @GetMapping("/username/{username}")
    public ResponseEntity<Boolean> checkUsernameDuplicated(@PathVariable String username) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.checkUsernameDuplicated(username));
    }

    // 닉네임 중복 확인
    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<Boolean> checkNicknameDuplicated(@PathVariable String nickname) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.checkNicknameDuplicated(nickname));
    }

    // ROLE 농부로 변경
    @PutMapping("/role")
    public ResponseEntity<Void> changeToFarmer(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        authService.changeToFarmer(userDetails.getMember());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
