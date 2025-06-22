package Uniton.Fring.domain.member.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "로그인 요청 DTO")
public class MypageRequestDto {

    @NotBlank(message = "닉네임이 비어있습니다.")
    @Schema(description = "사용자의 닉네임", example = "두옹균")
    private String nickname;

    @NotBlank(message = "소개글이 비어있습니다.")
    @Schema(description = "사용자의 소개글", example = "핑크솔트로 구운 삼겹살을 좋아해요")
    private String introduction;
}
