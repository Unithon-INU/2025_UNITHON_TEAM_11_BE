package Uniton.Fring.domain.member.dto.res;

import Uniton.Fring.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "회원가입 응답 DTO")
public class SignupResponseDto {

    @Schema(description = "회원가입된 아이디", example = "dongkyun")
    private final String username;

    @Schema(description = "회원가입된 닉네임", example = "두옹균")
    private final String nickname;

    @Builder
    private SignupResponseDto(Member member) {
        this.username = member.getUsername();
        this.nickname = member.getNickname();
    }
}