package Uniton.Fring.domain.recipe.dto.res;

import Uniton.Fring.domain.member.dto.res.MemberInfoResponseDto;
import Uniton.Fring.domain.recipe.entity.Recipe;
import Uniton.Fring.domain.review.dto.res.CommentResponseDto;
import Uniton.Fring.domain.review.dto.res.ReviewResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Schema(description = "레시피 상세 정보 응답 DTO")
public class RecipeInfoResponseDto {

    @Schema(description = "리뷰 작성자", example = "365일 다이어터")
    private final MemberInfoResponseDto member;

    @Schema(description = "좋아요 여부", example = "true")
    private final Boolean isLiked;

    @Schema(description = "좋아요 수", example = "1235")
    private final Integer likeCount;

    @Schema(description = "레시피 아이디", example = "1")
    private final Long id;

    @Schema(description = "레시피 제목", example = "차돌된장찌개")
    private final String title;

    @Schema(description = "레시피 설명", example = "차돌박이를 이용한 구수한 된장찌개 레시피입니다.")
    private final String content;

    @Schema(description = "레시피 대표 이미지 URL", example = "https://fring-s3.s3.ap-northeast-2.amazonaws.com/recipes/image.jpg")
    private final String imageUrl;

    @Schema(description = "레시피 평점", example = "4.7")
    private final Double rating;

    @Schema(description = "인분 수", example = "2")
    private final int headCount;

    @Schema(description = "조리 시간", example = "20분")
    private final String cookingTime;

    @Schema(description = "난이도", example = "중")
    private final String difficulty;

    @Schema(description = "레시피 생성 일시", example = "2024-06-17T14:30:00")
    private final LocalDateTime createdAt;

    @Schema(description = "재료 목록", example = "{\"두부\":\"1모\", \"된장\":\"2스푼\"}")
    private final Map<String, String> ingredients;

    @Schema(description = "양념 목록", example = "{\"간장\":\"1스푼\", \"다진마늘\":\"1스푼\"}")
    private final Map<String, String> sauces;

    @Schema(description = "요리 순서 목록")
    private final List<RecipeStepResponseDto> steps;

    @Schema(description = "레시피 후기", example = "~~~")
    private final List<ReviewResponseDto> reviews;

    @Schema(description = "총 리뷰수", example = "1234")
    private final Integer totalReviewCount;

    @Schema(description = "리뷰에 달린 사진들 (5개)", example = "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]")
    private final List<String> recentImageUrls;

    @Schema(description = "레시피 댓글", example = "~~~")
    private final List<CommentResponseDto> comments;

    @Builder
    private RecipeInfoResponseDto(MemberInfoResponseDto member,
                                  Boolean isLiked,
                                  Recipe recipe,
                                  List<RecipeStepResponseDto> recipeStep,
                                  List<ReviewResponseDto> reviews,
                                  Integer totalReviewCount,
                                  List<String> recentImageUrls,
                                  List<CommentResponseDto> comments) {
        this.member = member;
        this.isLiked = isLiked;
        this.likeCount = recipe.getLikeCount();
        this.id = recipe.getId();
        this.title = recipe.getTitle();
        this.content = recipe.getContent();
        this.imageUrl = recipe.getImageUrl();
        this.rating = formatRating(recipe.getRating());
        this.headCount = recipe.getHeadCount();
        this.cookingTime = recipe.getCookingTime();
        this.difficulty = recipe.getDifficulty();
        this.createdAt = recipe.getCreatedAt();
        this.ingredients = recipe.getIngredients();
        this.sauces = recipe.getSauces();
        this.steps = recipeStep;
        this.reviews = reviews;
        this.totalReviewCount = totalReviewCount;
        this.recentImageUrls = recentImageUrls;
        this.comments = comments;
    }

    private Double formatRating(Double rating) {
        if (rating == null) return 0.0;
        return Math.floor(rating * 10) / 10.0;
    }
}
