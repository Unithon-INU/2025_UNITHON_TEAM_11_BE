package Uniton.Fring.domain.member.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "로그인 요청 DTO")
public class MypageRequestDto {

    @Schema(description = "사용자의 닉네임", example = "두옹균")
    private String nickname;

    @Schema(description = "사용자의 소개글", example = "핑크솔트로 구운 삼겹살을 좋아해요")
    private String introduction;
}
