package Uniton.Fring.domain.member.dto.res;

import Uniton.Fring.domain.member.entity.Member;
import Uniton.Fring.domain.member.enums.MemberRole;
import Uniton.Fring.domain.recipe.dto.res.SimpleRecipeResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "마이페이지 정보 응답 DTO")
public class MypageDetailResponseDto {

    @Schema(description = "회원 Id", example = "1")
    private final Long memberId;

    @Schema(description = "회원 프로필 이미지 URL", example = "http://example.com/profile.jpg")
    private final String imageUrl;

    @Schema(description = "회원 닉네임", example = "핑크솔트123")
    private final String nickname;

    @Schema(description = "회원 소개글", example = "핑크솔트로 구운 삼겹살을 좋아해요")
    private final String introduction;

    @Schema(description = "판매자 여부", example = "true")
    private final Boolean isSeller;

    @Schema(description = "좋아요 수", example = "10")
    private final Integer likeCount;

    @Schema(description = "좋아요 여부", example = "true")
    private final Boolean isLiked;

    @Schema(description = "내 레시피 목록")
    private final List<SimpleRecipeResponseDto> simpleRecipeList;

    @Builder
    private MypageDetailResponseDto(Member member, Boolean isLiked, List<SimpleRecipeResponseDto> simpleRecipeList) {
        this.memberId = member.getId();
        this.imageUrl = member.getImageUrl();
        this.nickname = member.getNickname();
        this.introduction = member.getIntroduction();
        this.isSeller = member.getRole() == MemberRole.FARMER;
        this.likeCount = member.getLikeCount();
        this.isLiked = isLiked;
        this.simpleRecipeList = simpleRecipeList;
    }
}
