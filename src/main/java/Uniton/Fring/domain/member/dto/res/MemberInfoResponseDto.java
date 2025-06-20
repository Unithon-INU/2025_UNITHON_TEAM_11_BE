package Uniton.Fring.domain.member.dto.res;

import Uniton.Fring.domain.member.entity.Member;
import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.domain.recipe.dto.res.SimpleRecipeResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "회원 상세 정보 응답 DTO")
public class MemberInfoResponseDto {

    @Schema(description = "회원 Id", example = "1")
    private final Long memberId;

    @Schema(description = "회원 프로필 이미지 URL", example = "http://example.com/profile.jpg")
    private final String imageUrl;

    @Schema(description = "회원 닉네임", example = "핑크솔트123")
    private final String nickname;

    @Schema(description = "회원 소개글", example = "핑크솔트로 구운 삼겹살을 좋아해요")
    private final String introduction;

    @Schema(description = "회원 좋아요 수", example = "42")
    private final Integer likeCount;

    @Schema(description = "회원이 등록한 레시피 목록", implementation = SimpleRecipeResponseDto.class)
    private final List<SimpleRecipeResponseDto> recipes;

    @Schema(description = "회원이 등록한 농산물 목록", implementation = SimpleProductResponseDto.class)
    private final List<SimpleProductResponseDto> products;

    @Builder
    private MemberInfoResponseDto(Long memberId, String imageUrl, String nickname, String introduction,
                                 Integer likeCount, List<SimpleRecipeResponseDto> recipes, List<SimpleProductResponseDto> products) {
        this.memberId = memberId;
        this.imageUrl = imageUrl;
        this.nickname = nickname;
        this.introduction = introduction;
        this.likeCount = likeCount;
        this.recipes = recipes;
        this.products = products;
    }

    public static MemberInfoResponseDto fromMember(Member member) {
        return MemberInfoResponseDto.builder()
                .memberId(member.getId())
                .imageUrl(member.getImageUrl())
                .nickname(member.getNickname())
                .introduction(member.getIntroduction())
                .likeCount(member.getLikeCount())
                .recipes(null)
                .products(null)
                .build();
    }

    public static MemberInfoResponseDto fromConsumer(Member member, List<SimpleRecipeResponseDto> recipes) {
        return MemberInfoResponseDto.builder()
                .memberId(member.getId())
                .imageUrl(member.getImageUrl())
                .nickname(member.getNickname())
                .introduction(member.getIntroduction())
                .likeCount(member.getLikeCount())
                .recipes(recipes)
                .products(null)
                .build();
    }

    public static MemberInfoResponseDto fromFarmer(Member member, List<SimpleProductResponseDto> products) {
        return MemberInfoResponseDto.builder()
                .memberId(member.getId())
                .imageUrl(member.getImageUrl())
                .nickname(member.getNickname())
                .introduction(member.getIntroduction())
                .likeCount(member.getLikeCount())
                .recipes(null)
                .products(products)
                .build();
    }

    public static MemberInfoResponseDto fromReviewer(Member member) {
        return MemberInfoResponseDto.builder()
                .memberId(member.getId())
                .imageUrl(member.getImageUrl())
                .nickname(member.getNickname())
                .introduction(null)
                .likeCount(null)
                .recipes(null)
                .products(null)
                .build();
    }
}
