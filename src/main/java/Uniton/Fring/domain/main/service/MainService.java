package Uniton.Fring.domain.main.service;

import Uniton.Fring.domain.like.RecipeLikeRepository;
import Uniton.Fring.domain.main.dto.MainProductResponseDto;
import Uniton.Fring.domain.main.dto.MainRecipeResponseDto;
import Uniton.Fring.domain.main.dto.MainResponseDto;
import Uniton.Fring.domain.member.dto.res.MemberRankingResponseDto;
import Uniton.Fring.domain.member.entity.Member;
import Uniton.Fring.domain.member.repository.MemberRepository;
import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.domain.product.entity.Product;
import Uniton.Fring.domain.product.repository.ProductRepository;
import Uniton.Fring.domain.recipe.dto.res.SimpleRecipeResponseDto;
import Uniton.Fring.domain.recipe.dto.res.SpecialRecipeResponseDto;
import Uniton.Fring.domain.recipe.entity.Recipe;
import Uniton.Fring.domain.recipe.repository.RecipeRepository;
import Uniton.Fring.domain.review.ReviewRepository;
import Uniton.Fring.global.exception.CustomException;
import Uniton.Fring.global.exception.ErrorCode;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainService {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeLikeRepository recipeLikeRepository;
    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public MainResponseDto mainInfo(UserDetailsImpl userDetails) {

        log.info("[메인 페이지 정보 요청]");

        Long memberId;
        if (userDetails != null) { memberId = userDetails.getMember().getId(); } else {
            memberId = null;
        }

        log.info("[메인] 상품, 레시피 목록, 특별한 날 레시피 조회");
        List<Product> saleProducts = productRepository.findTop5ByOrderByDiscountRateDesc();
        List<Recipe> bestRecipes = recipeRepository.findTop5ByOrderByRatingDesc();
        Recipe specialRecipe = recipeRepository.findTop1ByTitleContainingOrderByCreatedAtDesc("특별")
                .orElseThrow(() -> {
                    log.warn("특별한 날 레시피 조회 실패");
                    return new CustomException(ErrorCode.RECIPE_NOT_FOUND);
                });

        log.info("[메인] 특가 농수산물 목록 응답 생성");
        List<SimpleProductResponseDto> simpleProductResponseDtos = saleProducts.stream()
                .map(product -> {
                    Boolean isLikedProduct = null;

                    if (memberId != null) {
                        isLikedProduct = productRepository.existsByMemberIdAndId(memberId, product.getId());
                    }

                    return SimpleProductResponseDto.builder().product(product).isLiked(isLikedProduct).build();
                }).toList();

        log.info("[메인] 평점 높은 레시피 목록 응답 생성");
        List<SimpleRecipeResponseDto> simpleRecipeResponseDtos = bestRecipes.stream()
                .map(recipe -> {
                    Boolean isLikedRecipe = null;
                    Integer reviewCount = reviewRepository.countByRecipeId(recipe.getId());

                    if (memberId != null) {
                        isLikedRecipe = recipeLikeRepository.existsByMemberIdAndRecipeId(memberId, recipe.getId());
                    }

                    return SimpleRecipeResponseDto.builder().recipe(recipe).isLiked(isLikedRecipe).commentCount(reviewCount).build();
                }).toList();

        log.info("[메인] 특별한 날 레시피 목록 응답 생성");
        Integer reviewCount = reviewRepository.countByRecipeId(specialRecipe.getId());
        SpecialRecipeResponseDto specialRecipeResponseDto = SpecialRecipeResponseDto.builder().recipe(specialRecipe).commentCount(reviewCount).build();

        log.info("[메인 페이지 정보 응답]");

        return MainResponseDto.builder().simpleProductResponseDtos(simpleProductResponseDtos).simpleRecipeResponseDtos(simpleRecipeResponseDtos).specialRecipeResponseDto(specialRecipeResponseDto).build();
    }

    public MainProductResponseDto mainProductInfo(UserDetailsImpl userDetails) {

        log.info("[장터 메인 페이지 정보 요청]");

        Long memberId;
        if (userDetails != null) { memberId = userDetails.getMember().getId(); } else {
            memberId = null;
        }

        List<Product> bestProducts = productRepository.findTop5ByOrderByRatingDesc();
        List<Product> frequentProducts = productRepository.findTopProductsByReviewCount(PageRequest.of(0, 5));

        log.info("[장터 메인] 추천 농수산물 목록 응답 생성");
        List<SimpleProductResponseDto> bestProductResponseDtos = bestProducts.stream()
                .map(product -> {
                    Boolean isLikedProduct = null;

                    if (memberId != null) {
                        isLikedProduct = productRepository.existsByMemberIdAndId(memberId, product.getId());
                    }

                    return SimpleProductResponseDto.builder().product(product).isLiked(isLikedProduct).build();
                }).toList();

        log.info("[장터 메인] 자주 구매한 상품 목록 응답 생성");
        List<SimpleProductResponseDto> frequentProductResponseDtos = frequentProducts.stream()
                .map(product -> {
                    Boolean isLikedProduct = null;

                    if (memberId != null) {
                        isLikedProduct = productRepository.existsByMemberIdAndId(memberId, product.getId());
                    }

                    return SimpleProductResponseDto.builder().product(product).isLiked(isLikedProduct).build();
                }).toList();

        log.info("[장터 메인 페이지 정보 응답]");

        return MainProductResponseDto.builder().bestProductResponseDtos(bestProductResponseDtos).frequentProductResponseDtos(frequentProductResponseDtos).build();
    }

    @Transactional(readOnly = true)
    public MainRecipeResponseDto mainRecipeInfo(UserDetailsImpl userDetails) {

        log.info("[레시피 메인 페이지 정보 요청]");

        Long memberId;
        if (userDetails != null) { memberId = userDetails.getMember().getId(); } else {
            memberId = null;
        }

        List<Recipe> bestRecipes = recipeRepository.findTop5ByOrderByRatingDesc();
        List<Member> members = memberRepository.findTop5ByOrderByLikeCountDesc();
        List<Recipe> newRecipes = recipeRepository.findTop5ByOrderByCreatedAtDesc();
        Recipe specialRecipe = recipeRepository.findTop1ByTitleContainingOrderByCreatedAtDesc("특별")
                .orElseThrow(() -> {
                    log.warn("특별한 날 레시피 조회 실패");
                    return new CustomException(ErrorCode.RECIPE_NOT_FOUND);
                });

        log.info("[레시피 메인] 핫한 레시피 목록 응답 생성");
        List<SimpleRecipeResponseDto> simpleRecipeResponseDtos = bestRecipes.stream()
                .map(recipe -> {
                    Boolean isLikedRecipe = null;
                    Integer reviewCount = reviewRepository.countByRecipeId(recipe.getId());

                    if (memberId != null) {
                        isLikedRecipe = recipeLikeRepository.existsByMemberIdAndRecipeId(memberId, recipe.getId());
                    }

                    return SimpleRecipeResponseDto.builder().recipe(recipe).isLiked(isLikedRecipe).commentCount(reviewCount).build();
                }).toList();

        log.info("[레시피 메인] 요리 선생님 추천 목록 응답 생성");
        List<MemberRankingResponseDto> memberRankingResponseDtos = members.stream()
                .map(member -> MemberRankingResponseDto.builder().member(member).build()).toList();

        log.info("[레시피 메인] 최신 레시피 목록 응답 생성");
        List<SimpleRecipeResponseDto> newRecipeResponseDtos = newRecipes.stream()
                .map(recipe -> {
                    Boolean isLikedRecipe = null;
                    Integer reviewCount = reviewRepository.countByRecipeId(recipe.getId());

                    if (memberId != null) {
                        isLikedRecipe = recipeLikeRepository.existsByMemberIdAndRecipeId(memberId, recipe.getId());
                    }

                    return SimpleRecipeResponseDto.builder().recipe(recipe).isLiked(isLikedRecipe).commentCount(reviewCount).build();
                }).toList();

        log.info("[메인] 특별한 날 레시피 목록 응답 생성");
        Integer reviewCount = reviewRepository.countByRecipeId(specialRecipe.getId());
        SpecialRecipeResponseDto specialRecipeResponseDto = SpecialRecipeResponseDto.builder().recipe(specialRecipe).commentCount(reviewCount).build();

        log.info("[레시피 메인 페이지 정보 응답]");

        return MainRecipeResponseDto.builder()
                .simpleRecipeResponseDtos(simpleRecipeResponseDtos).memberRankingResponseDtos(memberRankingResponseDtos)
                .newRecipeResponseDtos(newRecipeResponseDtos).specialRecipeResponseDto(specialRecipeResponseDto).build();
    }
}
