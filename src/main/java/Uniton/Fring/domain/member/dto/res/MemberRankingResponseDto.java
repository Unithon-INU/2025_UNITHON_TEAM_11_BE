package Uniton.Fring.domain.member.dto.res;

import Uniton.Fring.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberRankingResponseDto {

    private final Long memberId;

    private final String nickname;

    private final String imageUrl;

    private final Integer likeCount;

    @Builder
    private MemberRankingResponseDto(Member member) {
        this.memberId = member.getId();
        this.nickname = member.getNickname();
        this.imageUrl = member.getImageUrl();
        this.likeCount = member.getLikeCount();
    }
}
