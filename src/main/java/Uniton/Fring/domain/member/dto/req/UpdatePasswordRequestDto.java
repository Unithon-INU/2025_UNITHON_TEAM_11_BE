package Uniton.Fring.domain.member.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "입점 신청 요청 DTO")
public class UpdatePasswordRequestDto {

    @NotBlank(message = "비밀번호가 비어있습니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Schema(description = "현재 사용자의 비밀번호 (최소 8자 이상)", example = "password123")
    private String password;

    @NotBlank(message = "비밀번호가 비어있습니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Schema(description = "새로운 사용자의 비밀번호 (최소 8자 이상)", example = "password123")
    private String newPassword;
}
