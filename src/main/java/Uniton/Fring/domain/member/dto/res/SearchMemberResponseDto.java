package Uniton.Fring.domain.member.dto.res;

import Uniton.Fring.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "회원 검색 응답 DTO")
public class SearchMemberResponseDto {

    @Schema(description = "회원 ID", example = "1")
    private final Long memberId;

    @Schema(description = "닉네임", example = "핑크솔트123")
    private final String nickname;

    @Schema(description = "프로필 이미지 URL", example = "http://example.com/image.jpg")
    private final String imageUrl;

    @Schema(description = "회원 소개글", example = "핑크솔트로 구운 삼겹살을 좋아해요")
    private final String introduction;

    @Builder
    private SearchMemberResponseDto(Member member) {
        this.memberId = member.getId();
        this.nickname = member.getNickname();
        this.imageUrl = member.getImageUrl();
        this.introduction = member.getIntroduction();
    }
}
