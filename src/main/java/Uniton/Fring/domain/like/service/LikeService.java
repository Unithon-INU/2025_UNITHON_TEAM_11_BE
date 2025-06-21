package Uniton.Fring.domain.like.service;

import Uniton.Fring.domain.like.dto.res.LikeStatusResponseDto;
import Uniton.Fring.domain.like.entity.MemberLike;
import Uniton.Fring.domain.like.entity.ProductLike;
import Uniton.Fring.domain.like.entity.RecipeLike;
import Uniton.Fring.domain.like.entity.ReviewLike;
import Uniton.Fring.domain.like.repository.MemberLikeRepository;
import Uniton.Fring.domain.like.repository.ProductLikeRepository;
import Uniton.Fring.domain.like.repository.RecipeLikeRepository;
import Uniton.Fring.domain.like.repository.ReviewLikeRepository;
import Uniton.Fring.domain.member.entity.Member;
import Uniton.Fring.domain.member.repository.MemberRepository;
import Uniton.Fring.domain.product.entity.Product;
import Uniton.Fring.domain.product.repository.ProductRepository;
import Uniton.Fring.domain.recipe.entity.Recipe;
import Uniton.Fring.domain.recipe.repository.RecipeRepository;
import Uniton.Fring.domain.review.entity.Review;
import Uniton.Fring.domain.review.repository.ReviewRepository;
import Uniton.Fring.global.exception.CustomException;
import Uniton.Fring.global.exception.ErrorCode;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final RecipeRepository recipeRepository;
    private final ReviewRepository reviewRepository;
    private final MemberLikeRepository memberLikeRepository;
    private final ProductLikeRepository productLikeRepository;
    private final RecipeLikeRepository recipeLikeRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    @Transactional
    public LikeStatusResponseDto likeMember(UserDetailsImpl userDetails, Long targetMemberId) {

        log.info("[회원 좋아요 요청]");

        Long memberId = userDetails.getMember().getId();

        // 자기 자신 좋아요 방지
        if (memberId.equals(targetMemberId)) {
            throw new CustomException(ErrorCode.INVALID_LIKE_TARGET);
        }

        // 회원 존재 여부 체크
        Member targetMember = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        boolean isLiked = memberLikeRepository.existsByMemberIdAndLikedMemberId(memberId, targetMemberId);

        if (isLiked) {
            memberLikeRepository.deleteByMemberIdAndLikedMemberId(memberId, targetMemberId);
        } else {
            memberLikeRepository.save(MemberLike.builder()
                    .memberId(memberId)
                    .likedMemberId(targetMemberId)
                    .build());
        }

        int likeCount = memberLikeRepository.countByLikedMemberId(targetMemberId);

        log.info("[회원 좋아요 성공]");

        return LikeStatusResponseDto.builder()
                .name(targetMember.getNickname())
                .isLiked(!isLiked)
                .likeCount(likeCount)
                .build();
    }

    @Transactional
    public LikeStatusResponseDto likeProduct(UserDetailsImpl userDetails, Long productId) {
        log.info("[상품 좋아요 요청] productId={}", productId);

        Long memberId = userDetails.getMember().getId();

        // 상품 존재 확인
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        boolean isLiked = productLikeRepository.existsByMemberIdAndProductId(memberId, productId);

        if (isLiked) {
            productLikeRepository.deleteByMemberIdAndProductId(memberId, productId);
        } else {
            productLikeRepository.save(ProductLike.builder()
                    .memberId(memberId)
                    .productId(productId)
                    .build());
        }

        int likeCount = productLikeRepository.countByProductId(productId);

        log.info("[상품 좋아요 성공]");

        return LikeStatusResponseDto.builder()
                .name(product.getName())
                .isLiked(!isLiked)
                .likeCount(likeCount)
                .build();
    }

    @Transactional
    public LikeStatusResponseDto likeRecipe(UserDetailsImpl userDetails, Long recipeId) {
        log.info("[레시피 좋아요 요청] recipeId={}", recipeId);

        Long memberId = userDetails.getMember().getId();

        // 레시피 존재 확인
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new CustomException(ErrorCode.RECIPE_NOT_FOUND));

        boolean isLiked = recipeLikeRepository.existsByMemberIdAndRecipeId(memberId, recipeId);

        if (isLiked) {
            recipeLikeRepository.deleteByMemberIdAndRecipeId(memberId, recipeId);
        } else {
            recipeLikeRepository.save(RecipeLike.builder()
                    .memberId(memberId)
                    .recipeId(recipeId)
                    .build());
        }

        int likeCount = recipeLikeRepository.countByRecipeId(recipeId);

        log.info("[레시피 좋아요 성공]");

        return LikeStatusResponseDto.builder()
                .name(recipe.getTitle())
                .isLiked(!isLiked)
                .likeCount(likeCount)
                .build();
    }

    @Transactional
    public LikeStatusResponseDto likeReview(UserDetailsImpl userDetails, Long reviewId) {
        log.info("[리뷰 좋아요 요청] reviewId={}", reviewId);

        Long memberId = userDetails.getMember().getId();

        // 리뷰 존재 확인
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        boolean isLiked = reviewLikeRepository.existsByMemberIdAndReviewId(memberId, reviewId);

        if (isLiked) {
            reviewLikeRepository.deleteByMemberIdAndReviewId(memberId, reviewId);
        } else {
            reviewLikeRepository.save(ReviewLike.builder()
                    .memberId(memberId)
                    .reviewId(reviewId)
                    .build());
        }

        int likeCount = reviewLikeRepository.countByReviewId(reviewId);

        log.info("[리뷰 좋아요 처리 완료]");

        return LikeStatusResponseDto.builder()
                .name(review.getContent())
                .isLiked(!isLiked)
                .likeCount(likeCount)
                .build();
    }
}
