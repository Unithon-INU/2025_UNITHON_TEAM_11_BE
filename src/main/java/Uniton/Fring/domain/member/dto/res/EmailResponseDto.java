package Uniton.Fring.domain.member.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "로그인 응답 DTO")
public class EmailResponseDto {

    @Schema(description = "사용자의 이메일 주소", example = "dongkyun@gmail.com")
    private final String email;

    @Schema(description = "사용자의 인증번호", example = "562934")
    private final String authNumber;
}