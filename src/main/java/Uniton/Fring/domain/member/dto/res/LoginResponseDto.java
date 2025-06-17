package Uniton.Fring.domain.member.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
@Schema(description = "로그인 응답 DTO")
public class LoginResponseDto {

    @Schema(description = "사용자 이메일", example = "dongkyun@gmail.com")
    private final String email;

    @Schema(description = "회원 프로필 이미지 URL", example = "http://example.com/profile.jpg")
    private String imageUrl;

    @Schema(description = "회원 닉네임", example = "핑크솔트123")
    private String nickname;

    @Schema(description = "회원 소개글", example = "핑크솔트로 구운 삼겹살을 좋아해요")
    private String introduction;

    @Schema(description = "JWT Access Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private final String accessToken;

    @Schema(description = "JWT Refresh Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private final String refreshToken;

    @Schema(description = "Access Token 만료 시간 (Epoch milliseconds)", example = "7195000")
    private final Long accessTokenExpires;

    @Schema(description = "Access Token 만료 일시 (Date)", example = "2025-05-14T15:30:00.000+09:00")
    private final Date accessTokenExpiresDate;
}
