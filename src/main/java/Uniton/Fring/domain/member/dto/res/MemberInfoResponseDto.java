package Uniton.Fring.domain.member.dto.res;

import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.domain.member.entity.Member;
import Uniton.Fring.domain.recipe.dto.res.SimpleRecipeResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
@Schema(description = "회원 상세 정보 응답 DTO")
public class MemberInfoResponseDto {

    @Schema(description = "회원 프로필 이미지 URL", example = "http://example.com/profile.jpg")
    private String imageUrl;

    @Schema(description = "회원 닉네임", example = "핑크솔트123")
    private String nickname;

    @Schema(description = "회원 소개글", example = "핑크솔트로 구운 삼겹살을 좋아해요")
    private String introduction;

    @Schema(description = "회원 좋아요 수", example = "42")
    private Integer likeCount;

    @Schema(description = "회원이 등록한 레시피 목록", implementation = SimpleRecipeResponseDto.class)
    private List<SimpleRecipeResponseDto> recipes;

    @Schema(description = "회원이 등록한 농산물 목록", implementation = SimpleProductResponseDto.class)
    private List<SimpleProductResponseDto> products;

    public void MemberInfoFromConsumer(Member member, List<SimpleRecipeResponseDto> recipes) {
        this.imageUrl = member.getImageUrl();
        this.nickname = member.getNickname();
        this.introduction = member.getIntroduction();
        this.likeCount = member.getLikeCount();
        this.recipes = recipes;
        this.products = null;
    }

    public void MemberInfoFromFarmer(Member member, List<SimpleProductResponseDto> products) {
        this.imageUrl = member.getImageUrl();
        this.nickname = member.getNickname();
        this.introduction = member.getIntroduction();
        this.likeCount = member.getLikeCount();
        this.recipes = null;
        this.products = products;
    }
}
