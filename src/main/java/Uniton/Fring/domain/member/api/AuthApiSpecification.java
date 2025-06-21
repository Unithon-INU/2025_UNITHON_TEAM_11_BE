package Uniton.Fring.domain.member.api;

import Uniton.Fring.domain.member.dto.req.LoginRequestDto;
import Uniton.Fring.domain.member.dto.req.SignupRequestDto;
import Uniton.Fring.domain.member.dto.res.LoginResponseDto;
import Uniton.Fring.domain.member.dto.res.SignupResponseDto;
import Uniton.Fring.global.security.jwt.JwtTokenRequestDto;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Auth", description = "회원 인증 및 권한 관련 API")
public interface AuthApiSpecification {

    @Operation(
            summary = "회원가입 [ JWT ❌ ]",
            description = "회원가입을 진행합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "회원가입 성공"),
                    @ApiResponse(responseCode = "400", description = "비밀번호가 일치하지 않습니다."),
                    @ApiResponse(responseCode = "500", description = "파일 변환에 실패했습니다.")
            }
    )
    ResponseEntity<SignupResponseDto> signup(@RequestPart @Valid SignupRequestDto signupRequestDto,
                                             @RequestPart(value = "image", required = false) MultipartFile multipartFile);

    @Operation(
            summary = "로그인 [ JWT ❌ ]",
            description = "이메일과 비밀번호로 로그인을 시도합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "로그인 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LoginResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "멤버를 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "400", description = "비밀번호가 일치하지 않습니다."),
                    @ApiResponse(responseCode = "401", description = "로그인 실패 - 잘못된 자격 증명")
            }
    )
    ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto);

    @Operation(
            summary = "토큰 재발급",
            description = "Refresh Token을 재발급 합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "새로운 리프레시 토큰 반환",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LoginResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "420", description = "만료된 리프레시 토큰입니다."),
                    @ApiResponse(responseCode = "404", description = "리프레시 토큰 조회 실패"),
                    @ApiResponse(responseCode = "400", description = "리프레시 토큰 불일치")
            }
    )
    ResponseEntity<LoginResponseDto> refresh(@Valid @RequestBody JwtTokenRequestDto jwtTokenRequestDto);

    @Operation(
            summary = "이메일 중복 확인 [ JWT ❌ ]",
            description = "이메일이 이미 존재하는지 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "사용 가능 여부 반환",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Boolean.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "이메일이 중복되었습니다.")
            }
    )
    ResponseEntity<Boolean> checkEmailDuplicated(@PathVariable String email);

    @Operation(
            summary = "아이디 중복 확인 [ JWT ❌ ]",
            description = "아이디가 이미 존재하는지 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "사용 가능 여부 반환",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Boolean.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "아이디가 중복되었습니다.")
            }
    )
    ResponseEntity<Boolean> checkUsernameDuplicated(@PathVariable String username);

    @Operation(
            summary = "닉네임 중복 확인 [ JWT ❌ ]",
            description = "닉네임이 이미 존재하는지 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "사용 가능 여부 반환",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Boolean.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "닉네임이 중복되었습니다.")
            }
    )
    ResponseEntity<Boolean> checkNicknameDuplicated(@PathVariable String nickname);

    @Operation(
            summary = "멤버 ROLE을 농부로 변경",
            description = "회원의 ROLE을 농부로 변경합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공 여부 반환",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Boolean.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "멤버를 찾을 수 없습니다.")
            }
    )
    ResponseEntity<Void> changeToFarmer(@AuthenticationPrincipal UserDetailsImpl userDetails);

}
