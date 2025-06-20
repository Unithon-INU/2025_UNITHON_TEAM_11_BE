package Uniton.Fring.domain.recipe.service;

import Uniton.Fring.domain.like.RecipeLikeRepository;
import Uniton.Fring.domain.recipe.dto.req.RecipeRequestDto;
import Uniton.Fring.domain.recipe.dto.req.RecipeStepRequestDto;
import Uniton.Fring.domain.recipe.dto.res.RecipeInfoResponseDto;
import Uniton.Fring.domain.recipe.dto.res.RecipeStepResponseDto;
import Uniton.Fring.domain.recipe.dto.res.SimpleRecipeResponseDto;
import Uniton.Fring.domain.recipe.entity.Recipe;
import Uniton.Fring.domain.recipe.entity.RecipeStep;
import Uniton.Fring.domain.recipe.repository.RecipeRepository;
import Uniton.Fring.domain.recipe.repository.RecipeStepRepository;
import Uniton.Fring.domain.review.ReviewRepository;
import Uniton.Fring.global.exception.CustomException;
import Uniton.Fring.global.exception.ErrorCode;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeStepRepository recipeStepRepository;
    private final RecipeLikeRepository recipeLikeRepository;
    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public RecipeInfoResponseDto getRecipe(Long recipeId) {

        log.info("[레시피 조회 요청] recipeId={}", recipeId);

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> {
                    log.warn("[레시피 조회 실패] 레시피 없음: recipeId={}", recipeId);
                    return new CustomException(ErrorCode.RECIPE_NOT_FOUND);
                });

        List<RecipeStep> steps = recipeStepRepository.findByRecipeId(recipeId);

        List<RecipeStepResponseDto> recipeStepResponseDtos = steps.stream()
                        .map(step -> RecipeStepResponseDto.builder().recipeStep(step).build()).toList();

        log.info("[레시피 조회 성공] recipeId={}", recipeId);

        return RecipeInfoResponseDto.builder().recipe(recipe).recipeStep(recipeStepResponseDtos).build();
    }

    @Transactional(readOnly = true)
    public List<SimpleRecipeResponseDto> getBestRecipeList(UserDetailsImpl userDetails) {

        log.info("[요즘 핫한 레시피 더보기 요청]");

        Long memberId;
        if (userDetails != null) { memberId = userDetails.getMember().getId(); } else {
            memberId = null;
        }

        List<Recipe> bestRecipes = recipeRepository.findTop10ByOrderByRatingDesc();

        List<SimpleRecipeResponseDto> bestRecipeResponseDtos = bestRecipes.stream()
                .map(recipe -> {
                    Boolean isLikedRecipe = null;
                    Integer reviewCount = reviewRepository.countByRecipeId(recipe.getId());

                    if (memberId != null) {
                        isLikedRecipe = recipeLikeRepository.existsByMemberIdAndRecipeId(memberId, recipe.getId());
                    }

                    return SimpleRecipeResponseDto.builder().recipe(recipe).isLiked(isLikedRecipe).commentCount(reviewCount).build();
                }).toList();

        log.info("[요즘 핫한 레시피 더보기 성공]");

        return bestRecipeResponseDtos;
    }

    @Transactional(readOnly = true)
    public List<SimpleRecipeResponseDto> getRecipeList(UserDetailsImpl userDetails, int page) {

        log.info("[전체 레시피 목록 요청]");

        Pageable pageable = PageRequest.of(page, 8, Sort.by(Sort.Direction.DESC, "createdAt"));

        Long memberId;
        if (userDetails != null) { memberId = userDetails.getMember().getId(); } else {
            memberId = null;
        }

        Page<Recipe> recentRecipes = recipeRepository.findAll(pageable);
        log.info("레시피 8개 조회 완료");

        List<SimpleRecipeResponseDto> recentRecipeResponseDtos = recentRecipes.stream()
                .map(recipe -> {
                    Boolean isLikedRecipe = null;
                    Integer reviewCount = reviewRepository.countByRecipeId(recipe.getId());

                    if (memberId != null) {
                        isLikedRecipe = recipeLikeRepository.existsByMemberIdAndRecipeId(memberId, recipe.getId());
                    }

                    return SimpleRecipeResponseDto.builder().recipe(recipe).isLiked(isLikedRecipe).commentCount(reviewCount).build();
                }).toList();

        log.info("[전체 레시피 목록 조회 성공]");

        return recentRecipeResponseDtos;
    }

    @Transactional
    public RecipeInfoResponseDto addRecipe(UserDetailsImpl userDetails, RecipeRequestDto recipeRequestDto) {

        log.info("[레시피 등록 요청] recipeTitle={}", recipeRequestDto.getTitle());

        Recipe recipe = Recipe.builder().memberId(userDetails.getMember().getId()).recipeRequestDto(recipeRequestDto).build();
        recipeRepository.save(recipe);

        List<RecipeStepResponseDto> recipeStepResponseDtos = new ArrayList<>();

        log.info("[레시피 순서 추가] recipeTitle={},", recipeRequestDto.getTitle());
        for (RecipeStepRequestDto recipeStepRequestDto : recipeRequestDto.getSteps()) {
            RecipeStep recipeStep = RecipeStep.builder().recipeStepRequestDto(recipeStepRequestDto).recipeId(recipe.getId()).build();
            recipeStepRepository.save(recipeStep);
            recipeStepResponseDtos.add(RecipeStepResponseDto.builder().recipeStep(recipeStep).build());
        }

        log.info("[레시피 등록 성공] recipeTitle={}", recipeRequestDto.getTitle());

        return RecipeInfoResponseDto.builder().recipe(recipe).recipeStep(recipeStepResponseDtos).build();
    }

    @Transactional
    public RecipeInfoResponseDto updateRecipe(UserDetailsImpl userDetails, RecipeRequestDto recipeRequestDto, Long recipeId) {

        log.info("[레시피 수정 요청] recipeId={}", recipeId);

        Recipe recipe = recipeRepository.findById(recipeId)
                        .orElseThrow(() -> {
                            log.warn("[레시피 수정 실패] 레시피 없음: recipeId={}", recipeId);
                            return new CustomException(ErrorCode.RECIPE_NOT_FOUND);
                        });

        if (!recipe.getMemberId().equals(userDetails.getMember().getId())) {
            log.warn("[레시피 수정 실패] 해당 레시피를 작성한 멤버가 아님: recipeId={}, memberId={}", recipeId, userDetails.getMember().getId());
            throw new CustomException(ErrorCode.RECIPE_MEMBER_NOT_MATCH);
        }

        recipe.updateRecipe(recipeRequestDto);
        log.info("[레시피 엔티티 업데이트 성공] recipeId={}", recipeId);

        recipeStepRepository.deleteByRecipeId(recipeId);

        List<RecipeStepResponseDto> recipeStepResponseDtos = new ArrayList<>();
        log.info("[레시피 순서 추가] recipeTitle={},", recipeRequestDto.getTitle());
        for (RecipeStepRequestDto recipeStepRequestDto : recipeRequestDto.getSteps()) {
            RecipeStep recipeStep = RecipeStep.builder().recipeStepRequestDto(recipeStepRequestDto).recipeId(recipe.getId()).build();
            recipeStepRepository.save(recipeStep);
            recipeStepResponseDtos.add(RecipeStepResponseDto.builder().recipeStep(recipeStep).build());
        }

        log.info("[레시피 수정 성공] recipeId={}", recipeId);

        return RecipeInfoResponseDto.builder().recipe(recipe).recipeStep(recipeStepResponseDtos).build();
    }

    @Transactional
    public void deleteRecipe(UserDetailsImpl userDetails, Long recipeId) {

        log.info("[레시피 삭제 요청] recipeId={}", recipeId);

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> {
                    log.warn("[레시피 삭제 실패] 레시피 없음: recipeId={}", recipeId);
                    return new CustomException(ErrorCode.RECIPE_NOT_FOUND);
                });

        if (!recipe.getMemberId().equals(userDetails.getMember().getId())) {
            log.warn("[레시피 삭제 실패] 해당 레시피를 작성한 멤버가 아님: recipeId={}, memberId={}", recipeId, userDetails.getMember().getId());
            throw new CustomException(ErrorCode.RECIPE_MEMBER_NOT_MATCH);
        }

        recipeStepRepository.deleteByRecipeId(recipeId);
        recipeRepository.deleteById(recipeId);

        log.info("[레시피 삭제 성공] recipeId={}", recipeId);
    }
}
