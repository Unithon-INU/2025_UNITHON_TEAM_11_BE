package Uniton.Fring.domain.member.dto.res;

import Uniton.Fring.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "회원 랭킹 응답 DTO")
public class MemberRankingResponseDto {

    @Schema(description = "회원 ID", example = "1")
    private final Long memberId;

    @Schema(description = "닉네임", example = "핑크솔트123")
    private final String nickname;

    @Schema(description = "프로필 이미지 URL", example = "http://example.com/image.jpg")
    private final String imageUrl;

    @Schema(description = "좋아요 수", example = "42")
    private final Integer likeCount;

    @Builder
    private MemberRankingResponseDto(Member member) {
        this.memberId = member.getId();
        this.nickname = member.getNickname();
        this.imageUrl = member.getImageUrl();
        this.likeCount = member.getLikeCount();
    }
}
