package Uniton.Fring.domain.main.service;

import Uniton.Fring.domain.like.repository.MemberLikeRepository;
import Uniton.Fring.domain.like.repository.ProductLikeRepository;
import Uniton.Fring.domain.like.repository.RecipeLikeRepository;
import Uniton.Fring.domain.main.dto.res.MainProductResponseDto;
import Uniton.Fring.domain.main.dto.res.MainRecipeResponseDto;
import Uniton.Fring.domain.main.dto.res.MainResponseDto;
import Uniton.Fring.domain.main.dto.res.SearchAllResponseDto;
import Uniton.Fring.domain.member.dto.res.SimpleMemberResponseDto;
import Uniton.Fring.domain.member.entity.Member;
import Uniton.Fring.domain.member.repository.MemberRepository;
import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.domain.product.entity.Product;
import Uniton.Fring.domain.product.repository.ProductRepository;
import Uniton.Fring.domain.recipe.dto.res.SimpleRecipeResponseDto;
import Uniton.Fring.domain.recipe.dto.res.SpecialRecipeResponseDto;
import Uniton.Fring.domain.recipe.entity.Recipe;
import Uniton.Fring.domain.recipe.repository.RecipeRepository;
import Uniton.Fring.domain.recipe.service.RecipeService;
import Uniton.Fring.domain.review.repository.ReviewRepository;
import Uniton.Fring.domain.review.service.ReviewService;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainService {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final RecipeRepository recipeRepository;
    private final ReviewRepository reviewRepository;
    private final RecipeLikeRepository recipeLikeRepository;
    private final MemberLikeRepository memberLikeRepository;
    private final ProductLikeRepository productLikeRepository;
    private final RecipeService recipeService;
    private final ReviewService reviewService;

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
                .orElse(null);

        log.info("[메인] 특가 농수산물 목록 응답 생성");
        List<SimpleProductResponseDto> simpleProductResponseDtos = saleProducts.stream()
                .map(product -> {
                    Boolean isLikedProduct = null;

                    if (memberId != null) {
                        isLikedProduct = productLikeRepository.existsByMemberIdAndProductId(memberId, product.getId());
                    }

                    return SimpleProductResponseDto.builder().product(product).isLiked(isLikedProduct).build();
                }).toList();

        Map<Long, Integer> reviewCountMap = reviewService.getReviewCountMapFromRecipes(bestRecipes);

        log.info("[메인] 평점 높은 레시피 목록 응답 생성");
        List<SimpleRecipeResponseDto> simpleRecipeResponseDtos = bestRecipes.stream()
                .map(recipe -> {
                    Boolean isLikedRecipe = null;
                    Integer reviewCount = reviewCountMap.getOrDefault(recipe.getId(), 0);

                    if (memberId != null) {
                        isLikedRecipe = recipeLikeRepository.existsByMemberIdAndRecipeId(memberId, recipe.getId());
                    }

                    return SimpleRecipeResponseDto.builder().recipe(recipe).isLiked(isLikedRecipe).reviewCount(reviewCount).build();
                }).toList();

        SpecialRecipeResponseDto specialRecipeResponseDto;
        if (specialRecipe != null) {
            log.info("[메인] 특별한 날 레시피 목록 응답 생성");
            Integer reviewCount = reviewRepository.countByRecipeId(specialRecipe.getId());
            specialRecipeResponseDto = SpecialRecipeResponseDto.builder().recipe(specialRecipe).commentCount(reviewCount).build();
        } else {
            log.info("[메인] 특별한 날 레시피 목록이 존재하지 않습니다.");
            specialRecipeResponseDto = new SpecialRecipeResponseDto();
        }

        log.info("[메인 페이지 정보 응답]");

        return MainResponseDto.builder().simpleProductResponseDtos(simpleProductResponseDtos).simpleRecipeResponseDtos(simpleRecipeResponseDtos).specialRecipeResponseDto(specialRecipeResponseDto).build();
    }

    @Transactional(readOnly = true)
    public SearchAllResponseDto searchAll(UserDetailsImpl userDetails, String keyword, int page) {

        log.info("[메인 페이지 전체 검색 요청] keyword={}", keyword);

        Long memberId;
        if (userDetails != null) { memberId = userDetails.getMember().getId(); } else {
            memberId = null;
        }

        Pageable pageable = PageRequest.of(page, 3);

        Page<Member> memberPage = memberRepository.findByNicknameContaining(keyword, pageable);
        Page<Product> productPage = productRepository.findByNameContaining(keyword, pageable);
        Page<Recipe> recipePage = recipeRepository.findByTitleContaining(keyword, pageable);

        List<SimpleMemberResponseDto> memberResponseDtos = memberPage.stream()
                .map(member -> {
                    Boolean isLikedMember = null;

                    if (memberId != null) {
                        isLikedMember = memberLikeRepository.existsByMemberIdAndLikedMemberId(memberId, member.getId());
                    }

                    return SimpleMemberResponseDto.builder().member(member).isLikedMember(isLikedMember).build();
                }).toList();

        List<SimpleProductResponseDto> productResponseDtos = productPage.stream()
                .map(product -> {
                    Boolean isLikedProduct = null;

                    if (memberId != null) {
                        isLikedProduct = productLikeRepository.existsByMemberIdAndProductId(memberId, product.getId());
                    }

                    return SimpleProductResponseDto.builder().product(product).isLiked(isLikedProduct).build();
                }).toList();

        // 리뷰 수 추출
        Map<Long, Integer> reviewCountMap = reviewService.getReviewCountMapFromRecipes(recipePage.getContent());

        List<SimpleRecipeResponseDto> recipeResponseDtos = recipePage.stream()
                .map(recipe -> {
                    Boolean isLikedRecipe = null;
                    Integer reviewCount = reviewCountMap.getOrDefault(recipe.getId(), 0);

                    if (memberId != null) {
                        isLikedRecipe = recipeLikeRepository.existsByMemberIdAndRecipeId(memberId, recipe.getId());
                    }

                    return SimpleRecipeResponseDto.builder().recipe(recipe).isLiked(isLikedRecipe).reviewCount(reviewCount).build();
                }).toList();

        log.info("[메인 페이지 전체 검색 응답]");

        return SearchAllResponseDto.builder()
                .members(memberResponseDtos)
                .products(productResponseDtos)
                .recipes(recipeResponseDtos)
                .build();
    }

    @Transactional(readOnly = true)
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
                        isLikedProduct = productLikeRepository.existsByMemberIdAndProductId(memberId, product.getId());
                    }

                    return SimpleProductResponseDto.builder().product(product).isLiked(isLikedProduct).build();
                }).toList();

        log.info("[장터 메인] 자주 구매한 상품 목록 응답 생성");
        List<SimpleProductResponseDto> frequentProductResponseDtos = frequentProducts.stream()
                .map(product -> {
                    Boolean isLikedProduct = null;

                    if (memberId != null) {
                        isLikedProduct = productLikeRepository.existsByMemberIdAndProductId(memberId, product.getId());
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
        List<Member> members = memberRepository.findTop8ByIsRecipeMemberTrueOrderByLikeCountDesc();
        List<Recipe> newRecipes = recipeRepository.findTop5ByOrderByCreatedAtDesc();
        Recipe specialRecipe = recipeRepository.findTop1ByTitleContainingOrderByCreatedAtDesc("특별")
                .orElse(null);

        Map<Long, Integer> bestRecipeReviewCountMap = reviewService.getReviewCountMapFromRecipes(bestRecipes);
        Map<Long, Integer> newRecipeReviewCountMap = reviewService.getReviewCountMapFromRecipes(newRecipes);

        log.info("[레시피 메인] 핫한 레시피 목록 응답 생성");
        List<SimpleRecipeResponseDto> simpleRecipeResponseDtos = bestRecipes.stream()
                .map(recipe -> {
                    Boolean isLikedRecipe = null;
                    Integer reviewCount = bestRecipeReviewCountMap.getOrDefault(recipe.getId(), 0);

                    if (memberId != null) {
                        isLikedRecipe = recipeLikeRepository.existsByMemberIdAndRecipeId(memberId, recipe.getId());
                    }

                    return SimpleRecipeResponseDto.builder().recipe(recipe).isLiked(isLikedRecipe).reviewCount(reviewCount).build();
                }).toList();

        log.info("[레시피 메인] 요리 선생님 추천 목록 응답 생성");
        List<SimpleMemberResponseDto> simpleMemberResponseDtos = members.stream()
                .map(member -> {
                    Boolean isLikedMember = null;

                    if (memberId != null) {
                        isLikedMember = memberLikeRepository.existsByMemberIdAndLikedMemberId(memberId, member.getId());
                    }

                    return SimpleMemberResponseDto.builder().member(member).isLikedMember(isLikedMember).build();
                }).toList();

        log.info("[레시피 메인] 최신 레시피 목록 응답 생성");
        List<SimpleRecipeResponseDto> newRecipeResponseDtos = newRecipes.stream()
                .map(recipe -> {
                    Boolean isLikedRecipe = null;
                    Integer reviewCount = newRecipeReviewCountMap.getOrDefault(recipe.getId(), 0);

                    if (memberId != null) {
                        isLikedRecipe = recipeLikeRepository.existsByMemberIdAndRecipeId(memberId, recipe.getId());
                    }

                    return SimpleRecipeResponseDto.builder().recipe(recipe).isLiked(isLikedRecipe).reviewCount(reviewCount).build();
                }).toList();

        SpecialRecipeResponseDto specialRecipeResponseDto;
        if (specialRecipe != null) {
            log.info("[레시피 메인] 특별한 날 레시피 목록 응답 생성");
            Integer reviewCount = reviewRepository.countByRecipeId(specialRecipe.getId());
            specialRecipeResponseDto = SpecialRecipeResponseDto.builder().recipe(specialRecipe).commentCount(reviewCount).build();
        } else {
            log.info("[레시피 메인] 특별한 날 레시피 목록이 존재하지 않습니다.");
            specialRecipeResponseDto = new SpecialRecipeResponseDto();
        }

        log.info("[레시피 메인 페이지 정보 응답]");

        return MainRecipeResponseDto.builder()
                .simpleRecipeResponseDtos(simpleRecipeResponseDtos).simpleMemberResponseDtos(simpleMemberResponseDtos)
                .newRecipeResponseDtos(newRecipeResponseDtos).specialRecipeResponseDto(specialRecipeResponseDto).build();
    }
}
