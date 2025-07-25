package Uniton.Fring.domain.review.service;

import Uniton.Fring.domain.member.dto.res.MemberInfoResponseDto;
import Uniton.Fring.domain.product.entity.Product;
import Uniton.Fring.domain.product.repository.ProductRepository;
import Uniton.Fring.domain.recipe.entity.Recipe;
import Uniton.Fring.domain.recipe.repository.RecipeRepository;
import Uniton.Fring.domain.review.dto.req.ProductReviewRequestDto;
import Uniton.Fring.domain.review.dto.req.RecipeReviewRequestDto;
import Uniton.Fring.domain.review.dto.res.ReviewResponseDto;
import Uniton.Fring.domain.review.entity.Review;
import Uniton.Fring.domain.review.repository.ReviewRepository;
import Uniton.Fring.global.exception.CustomException;
import Uniton.Fring.global.exception.ErrorCode;
import Uniton.Fring.global.s3.S3Service;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final S3Service s3Service;
    private final ProductRepository productRepository;
    private final RecipeRepository recipeRepository;

    @Transactional
    public ReviewResponseDto addProductReview(UserDetailsImpl userDetails,
                                              ProductReviewRequestDto productReviewRequestDto,
                                              List<MultipartFile> images) {

        log.info("[상품 리뷰 등록 요청]");

        List<String> imageUrls =s3Service.uploadImages(images, "reviews");

        Review review = Review.fromProductReview(userDetails.getMember().getId(), productReviewRequestDto, imageUrls, productReviewRequestDto.getPurchase_option());

        reviewRepository.save(review);

        MemberInfoResponseDto memberInfoResponseDto = MemberInfoResponseDto.fromMember(userDetails.getMember(), null);

        Product product = productRepository.findById(productReviewRequestDto.getProductId())
                .orElseThrow(() -> {
                    log.warn("[농수산품 수정 실패] 상품 없음: productId={}", productReviewRequestDto.getProductId());
                    return new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
                });

        // 평균 평점 계산
        Double avgRating = reviewRepository.findAverageRatingByProductId(productReviewRequestDto.getProductId());
        product.updateRating(avgRating);

        log.info("[상품 리뷰 등록 성공]");

        return ReviewResponseDto.builder()
                .memberInfo(memberInfoResponseDto)
                .review(review)
                .isLiked(null)
                .purchaseOption(productReviewRequestDto.getPurchase_option())
                .build();
    }

    @Transactional
    public ReviewResponseDto addRecipeReview(UserDetailsImpl userDetails,
                                             RecipeReviewRequestDto recipeReviewRequestDto,
                                             List<MultipartFile> images) {

        log.info("[레시피 리뷰 등록 요청]");

        // 이미 레시피 리뷰가 존재하는지 확인
        boolean alreadyReviewed = reviewRepository.existsByMemberIdAndRecipeId(userDetails.getMember().getId(), recipeReviewRequestDto.getRecipeId());
        if (alreadyReviewed) {
            throw new CustomException(ErrorCode.ALREADY_REVIEWED);
        }

        List<String> imageUrls = s3Service.uploadImages(images, "reviews");

        Review review = Review.fromRecipeReview(userDetails.getMember().getId(), recipeReviewRequestDto, imageUrls);

        reviewRepository.save(review);

        MemberInfoResponseDto memberInfoResponseDto = MemberInfoResponseDto.fromMember(userDetails.getMember(), null);

        Recipe recipe = recipeRepository.findById(recipeReviewRequestDto.getRecipeId())
                .orElseThrow(() -> {
                    log.warn("[레시피 조회 실패] 레시피 없음: recipeId={}", recipeReviewRequestDto.getRecipeId());
                    return new CustomException(ErrorCode.RECIPE_NOT_FOUND);
                });

        Double avgRating = reviewRepository.findAverageRatingByRecipeId(recipeReviewRequestDto.getRecipeId());
        recipe.updateRating(avgRating);

        log.info("[레시피 리뷰 등록 성공]");

        return ReviewResponseDto.builder()
                .memberInfo(memberInfoResponseDto)
                .review(review)
                .isLiked(null)
                .purchaseOption(null)
                .build();
    }

    @Transactional(readOnly = true)
    public Map<Long, Integer> getReviewCountMapFromRecipes(List<Recipe> recipes) {
        List<Long> recipeIds = recipes.stream().map(Recipe::getId).toList();

        List<Object[]> reviewCounts = reviewRepository.countReviewsByRecipeIds(recipeIds);

        return reviewCounts.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((Long) row[1]).intValue()
                ));
    }
}
