package Uniton.Fring.domain.recipe.controller;

import Uniton.Fring.domain.recipe.dto.req.RecipeRequestDto;
import Uniton.Fring.domain.recipe.dto.res.BestRecipeResponseDto;
import Uniton.Fring.domain.recipe.dto.res.RecipeInfoResponseDto;
import Uniton.Fring.domain.recipe.dto.res.SimpleRecipeResponseDto;
import Uniton.Fring.domain.recipe.service.RecipeService;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recipes")
public class RecipeController implements RecipeApiSpecification{

    private final RecipeService recipeService;

    // 레시피 조회
    @GetMapping("/{recipeId}")
    public ResponseEntity<RecipeInfoResponseDto> getRecipe(@PathVariable Long recipeId) {
        return ResponseEntity.status(HttpStatus.OK).body(recipeService.getRecipe(recipeId));
    }

    // 레시피 목록 조회
    @GetMapping("/list")
    public ResponseEntity<List<SimpleRecipeResponseDto>> getRecipeList(@PageableDefault(size = 6) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(recipeService.getRecipeList(pageable));
    }

    // 레시피 추가
    @PostMapping
    public ResponseEntity<RecipeInfoResponseDto> addRecipe(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody RecipeRequestDto recipeRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recipeService.addRecipe(userDetails, recipeRequestDto));
    }

    // 레시피 수정
    @PutMapping("/{recipeId}")
    public ResponseEntity<RecipeInfoResponseDto> updateRecipe(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody RecipeRequestDto recipeRequestDto, @PathVariable Long recipeId) {
        return ResponseEntity.status(HttpStatus.OK).body(recipeService.updateRecipe(userDetails, recipeRequestDto, recipeId));
    }

    // 레시피 삭제
    @DeleteMapping("/{recipeId}")
    public ResponseEntity<Void> deleteRecipe(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long recipeId) {
        recipeService.deleteRecipe(userDetails, recipeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 평점이 좋은 레시피 상위 10개 반환
    @GetMapping("/rate")
    public ResponseEntity<List<BestRecipeResponseDto>> getBestRecipe() {
        return ResponseEntity.status(HttpStatus.OK).body(recipeService.getBestRecipe());
    }
}
