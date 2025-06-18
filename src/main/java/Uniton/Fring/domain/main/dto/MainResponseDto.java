package Uniton.Fring.domain.main.dto;

import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.domain.recipe.dto.res.SimpleRecipeResponseDto;
import Uniton.Fring.domain.recipe.dto.res.SpecialRecipeResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "메인 페이지 응답 DTO")
public class MainResponseDto {

    @Schema(description = "특가 농산물 목록", example = "[" +
            "{\"id\": 1, \"name\": \"신선한 사과\", \"price\": 10000, \"salePrice\": 8000, \"image\": \"https://your-bucket.s3.ap-northeast-2.amazonaws.com/products/apple.jpg\", \"isLiked\": true}," +
            "{\"id\": 2, \"name\": \"달콤한 딸기\", \"price\": 12000, \"salePrice\": 9000, \"image\": \"https://your-bucket.s3.ap-northeast-2.amazonaws.com/products/strawberry.jpg\", \"isLiked\": false}" +
            "]")
    private final List<SimpleProductResponseDto> simpleProductResponseDtos;

    @Schema(description = "평점 좋은 레시피 목록", example = "[" +
            "{\"id\": 1, \"title\": \"고소한 참치 마요 덮밥\", \"image\": \"https://fring-s3.s3.ap-northeast-2.amazonaws.com/recipes/image.jpg\", \"time\": \"15분\", \"rating\": 4.8, \"isLiked\": true, \"comment\": 12}," +
            "{\"id\": 2, \"title\": \"매콤한 떡볶이\", \"image\": \"https://fring-s3.s3.ap-northeast-2.amazonaws.com/recipes/tteokbokki.jpg\", \"time\": \"20분\", \"rating\": 4.5, \"isLiked\": false, \"comment\": 8}" +
            "]")
    private final List<SimpleRecipeResponseDto> simpleRecipeResponseDtos;

    @Schema(description = "메인 페이지 특별한 레시피", example =
            "{\"id\": 1, \"title\": \"고소한 참치 마요 덮밥\", \"content\": \"안녕하세요! 오늘은 토마토를 이용한 요리를 해보려고 합니다.\", \"image\": \"https://fring-s3.s3.ap-northeast-2.amazonaws.com/recipes/image.jpg\", \"time\": \"15분\", \"rating\": 4.8, \"comment\": 12}")
    private final SpecialRecipeResponseDto specialRecipeResponseDto;
}
