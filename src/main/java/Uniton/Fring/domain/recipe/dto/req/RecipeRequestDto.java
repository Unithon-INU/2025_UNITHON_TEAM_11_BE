package Uniton.Fring.domain.recipe.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@Schema(description = "레시피 요청 DTO")
public class RecipeRequestDto {

    @Schema(description = "레시피 제목", example = "맛있는 된장찌개")
    private String title;

    @Schema(description = "레시피 내용", example = "재료 손질부터 끓이기까지 상세 과정")
    private String content;

    @Schema(description = "레시피 대표 이미지 URL", example = "http://example.com/image.jpg")
    private String imageUrl;

    @Schema(description = "레시피 평점", example = "4.5")
    private Double rating;

    @Schema(description = "몇 명이 먹을 수 있는지", example = "4")
    private int headCount;

    @Schema(description = "조리 시간", example = "30분")
    private String cookingTime;

    @Schema(description = "난이도", example = "중간")
    private String difficulty;

    @Schema(description = "재료 목록 (재료명: 양)", example = "{\"감자\":\"2개\", \"양파\":\"1개\"}")
    private Map<String, String> ingredients;

    @Schema(description = "양념 목록 (양념명: 양)", example = "{\"된장\":\"3큰술\", \"고추장\":\"1큰술\"}")
    private Map<String, String> sauces;

    @Schema(description = "조리 단계 리스트")
    private List<RecipeStepRequestDto> steps;
}