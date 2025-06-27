package Uniton.Fring.domain.main.dto.res;

import Uniton.Fring.domain.member.dto.res.SimpleMemberResponseDto;
import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.domain.recipe.dto.res.SimpleRecipeResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "전체 검색 응답 DTO")
public class SearchAllResponseDto {

    @Schema(description = "레시피 회원 목록 응답", example = "하루포케, 아이와, 장발장")
    private final List<SimpleMemberResponseDto> recipeMembers;

    @Schema(description = "판매자 회원 목록 응답", example = "하루포케, 아이와, 장발장")
    private final List<SimpleMemberResponseDto> farmerMembers;

    @Schema(description = "상품 목록 응답", example = "계란, 브로콜리, 아보카도")
    private final List<SimpleProductResponseDto> products;

    @Schema(description = "레시피 목록 응답", example = "건강 피자, 아보카도 샌드위치, 햄부기")
    private final List<SimpleRecipeResponseDto> recipes;

    @Builder
    private SearchAllResponseDto(List<SimpleMemberResponseDto> recipeMembers,List<SimpleMemberResponseDto> farmerMembers ,List<SimpleProductResponseDto> products, List<SimpleRecipeResponseDto> recipes) {
        this.recipeMembers = recipeMembers;
        this.farmerMembers = farmerMembers;
        this.products = products;
        this.recipes = recipes;
    }
}
