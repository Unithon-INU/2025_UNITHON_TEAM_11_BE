package Uniton.Fring.domain.member.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "이메일 인증 수행 DTO")
public class EmailCheckDto {

    @NotBlank(message = "이메일이 비어있습니다.")
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    @Schema(description = "사용자의 이메일 주소", example = "dongkyun@gmail.com")
    private String email;

    @NotBlank(message = "인증 번호가 비어있습니다.")
    @Schema(description = "사용자의 인증번호", example = "562934")
    private String authNum;
}
