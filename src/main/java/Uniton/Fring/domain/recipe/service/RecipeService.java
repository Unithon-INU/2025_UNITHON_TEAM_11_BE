package Uniton.Fring.domain.recipe.service;

import Uniton.Fring.domain.member.entity.Member;
import Uniton.Fring.domain.member.repository.MemberRepository;
import Uniton.Fring.domain.recipe.dto.req.RecipeRequestDto;
import Uniton.Fring.domain.recipe.dto.req.RecipeStepRequestDto;
import Uniton.Fring.domain.recipe.dto.res.BestRecipeResponseDto;
import Uniton.Fring.domain.recipe.dto.res.RecipeInfoResponseDto;
import Uniton.Fring.domain.recipe.dto.res.RecipeStepResponseDto;
import Uniton.Fring.domain.recipe.dto.res.SimpleRecipeResponseDto;
import Uniton.Fring.domain.recipe.entity.Recipe;
import Uniton.Fring.domain.recipe.entity.RecipeStep;
import Uniton.Fring.domain.recipe.repository.RecipeRepository;
import Uniton.Fring.domain.recipe.repository.RecipeStepRepository;
import Uniton.Fring.global.exception.CustomException;
import Uniton.Fring.global.exception.ErrorCode;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeStepRepository recipeStepRepository;
    private final MemberRepository memberRepository;

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
    public List<SimpleRecipeResponseDto> getRecipeList(Pageable pageable) {

        log.info("[레시피 목록 요청]");

        Page<Recipe> recipes = recipeRepository.findAllByOrderByCreatedAtDesc(pageable);

        List<SimpleRecipeResponseDto> simpleRecipeResponseDtos = recipes.stream()
                .map(recipe -> SimpleRecipeResponseDto.builder().recipe(recipe).build()).toList();

        log.info("[레시피 목록 조회 성공]");

        return simpleRecipeResponseDtos;
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

    @Transactional(readOnly = true)
    public List<BestRecipeResponseDto> getBestRecipe() {

        log.info("[평점 기준 상위 10개 레시피 조회 요청]");

        List<Recipe> recipes = recipeRepository.findTop10ByOrderByRatingDesc();

        // distinct()를 통해 중복되는 회원 ID를 제거하여, 회원 조회 쿼리의 중복 실행을 막음
        List<Long> memberIds = recipes.stream().map(Recipe::getMemberId).distinct().toList();

        // 회원 ID 리스트를 이용해 회원 정보를 한 번에 DB에서 조회
        // 조회된 Member 객체들을 회원 ID를 키로, Member 객체를 값으로 하는 Map으로 변환
        Map<Long, Member> memberMap = memberRepository.findAllById(memberIds).stream()
                .collect(Collectors.toMap(Member::getId, Function.identity()));

        log.info("[응답 객체 생성] 각각 멤버의 닉네임 조회");
        List<BestRecipeResponseDto> bestRecipeResponseDtos = recipes.stream()
                .map(recipe -> {
                    Member member = memberMap.get(recipe.getMemberId());
                    if (member == null) {
                        throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
                    }

                    return BestRecipeResponseDto.builder().recipes(recipe).nickname(member.getNickname()).build();
                }).toList();

        log.info("[평점 기준 상위 10개 레시피 조회 성공]");

        return bestRecipeResponseDtos;
    }
}
