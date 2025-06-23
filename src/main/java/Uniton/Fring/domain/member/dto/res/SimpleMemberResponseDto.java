package Uniton.Fring.domain.member.dto.res;

import Uniton.Fring.domain.member.entity.Member;
import Uniton.Fring.domain.member.enums.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "회원 검색 응답 DTO")
public class SimpleMemberResponseDto {

    @Schema(description = "회원 ID", example = "1")
    private final Long memberId;

    @Schema(description = "닉네임", example = "핑크솔트123")
    private final String nickname;

    @Schema(description = "프로필 이미지 URL", example = "http://example.com/image.jpg")
    private final String imageUrl;

    @Schema(description = "회원 소개글", example = "핑크솔트로 구운 삼겹살을 좋아해요")
    private final String introduction;

    @Schema(description = "좋아요 수", example = "11345")
    private final Integer likeCount;

    @Schema(description = "좋아요 여부", example = "true")
    private final Boolean isLiked;

    @Schema(description = "판매자 여부", example = "true")
    private final Boolean isSeller;

    @Builder
    private SimpleMemberResponseDto(Member member, Integer likeCount, Boolean isLikedMember) {
        this.memberId = member.getId();
        this.nickname = member.getNickname();
        this.imageUrl = member.getImageUrl();
        this.introduction = member.getIntroduction();
        this.likeCount = likeCount;
        this.isLiked = isLikedMember;
        this.isSeller = member.getRole() == MemberRole.FARMER;
    }
}
