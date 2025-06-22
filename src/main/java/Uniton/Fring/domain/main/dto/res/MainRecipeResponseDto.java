package Uniton.Fring.domain.main.dto.res;

import Uniton.Fring.domain.member.dto.res.SimpleMemberResponseDto;
import Uniton.Fring.domain.recipe.dto.res.SimpleRecipeResponseDto;
import Uniton.Fring.domain.recipe.dto.res.SpecialRecipeResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "레시피 메인 페이지 응답 DTO")
public class MainRecipeResponseDto {

    @Schema(description = "핫한 레시피 목록", example = "[" +
            "{\"id\": 1, \"title\": \"고소한 참치 마요 덮밥\", \"image\": \"https://fring-s3.s3.ap-northeast-2.amazonaws.com/recipes/mayo.jpg\", \"time\": \"15분\", \"rating\": 4.8, \"isLiked\": true, \"comment\": 12}," +
            "{\"id\": 2, \"title\": \"매콤한 떡볶이\", \"image\": \"https://fring-s3.s3.ap-northeast-2.amazonaws.com/recipes/tteokbokki.jpg\", \"time\": \"20분\", \"rating\": 4.5, \"isLiked\": false, \"comment\": 8}" +
            "]")
    private final List<SimpleRecipeResponseDto> simpleRecipeResponseDtos;

    @Schema(description = "요리 선생님 추천 목록", example = "[" +
            "{\"memberId\": 1, \"nickname\": \"핑크솔트123\", \"imageUrl\": \"http://example.com/image.jpg\", \"likeCount\": 42}," +
            "{\"memberId\": 2, \"nickname\": \"그린허브456\", \"imageUrl\": \"http://example.com/image2.jpg\", \"likeCount\": 38}" +
            "]")
    private final List<SimpleMemberResponseDto> simpleMemberResponseDtos;

    @Schema(description = "최신 레시피 목록", example = "[" +
            "{\"id\": 3, \"title\": \"간단한 계란 볶음밥\", \"image\": \"https://fring-s3.s3.ap-northeast-2.amazonaws.com/recipes/friedrice.jpg\", \"time\": \"10분\", \"rating\": 4.6, \"isLiked\": true, \"comment\": 5}," +
            "{\"id\": 4, \"title\": \"토마토 달걀탕\", \"image\": \"https://fring-s3.s3.ap-northeast-2.amazonaws.com/recipes/egg_soup.jpg\", \"time\": \"12분\", \"rating\": 4.3, \"isLiked\": false, \"comment\": 3}" +
            "]")
    private final List<SimpleRecipeResponseDto> newRecipeResponseDtos;

    @Schema(description = "특별한 날 레시피 목록", example =
            "{\"id\": 5, \"title\": \"풍성한 명절 잡채\", \"content\": \"오늘은 명절에 잘 어울리는 잡채를 만들어볼게요.\", \"image\": \"https://fring-s3.s3.ap-northeast-2.amazonaws.com/recipes/japchae.jpg\", \"time\": \"30분\", \"rating\": 4.9, \"comment\": 21}")
    private final SpecialRecipeResponseDto specialRecipeResponseDto;
}
