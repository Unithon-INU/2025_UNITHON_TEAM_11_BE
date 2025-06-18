package Uniton.Fring.domain.recipe.controller;

import Uniton.Fring.domain.recipe.dto.req.RecipeRequestDto;
import Uniton.Fring.domain.recipe.dto.res.RecipeInfoResponseDto;
import Uniton.Fring.domain.recipe.dto.res.SimpleRecipeResponseDto;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Recipe", description = "레시피 관련 API")
public interface RecipeApiSpecification {

    @Operation(
            summary = "레시피 조회",
            description = "레시피를 아이디를 통해 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "레시피 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RecipeInfoResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "레시피를 찾을 수 없습니다.")
            }
    )
    ResponseEntity<RecipeInfoResponseDto> getRecipe(@PathVariable Long recipeId);

    @Operation(
            summary = "요즘 핫한 레시피 더보기 조회",
            description = "요즘 핫한 레시피 더보기 목록을 조회합니다. (레시피 10개)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "요즘 핫한 레시피 더보기 목록 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleRecipeResponseDto.class, type = "array")
                            )
                    )
            }
    )
    ResponseEntity<List<SimpleRecipeResponseDto>> getBestRecipeList(@AuthenticationPrincipal UserDetailsImpl userDetails);

    @Operation(
            summary = "전체 레시피 목록 조회 (지금 올라온 레시피)",
            description = "전체 레시피 목록을 조회합니다. (기본값: 레시피 8개)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "전체 레시피 목록 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleRecipeResponseDto.class, type = "array")
                            )
                    )
            }
    )
    ResponseEntity<List<SimpleRecipeResponseDto>> getRecipeList(@AuthenticationPrincipal UserDetailsImpl userDetails, @PageableDefault(size = 8) Pageable pageable);

    @Operation(
            summary = "레시피 추가",
            description = "레시피를 추가(등록)합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "레시피 추가 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RecipeInfoResponseDto.class)
                            )
                    )
            }
    )
    ResponseEntity<RecipeInfoResponseDto> addRecipe(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody RecipeRequestDto recipeRequestDto);

    @Operation(
            summary = "레시피 수정",
            description = "본인이 작성한 레시피를 수정합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "레시피 수정 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RecipeInfoResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "레시피를 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "403", description = "레시피에 접근할 권한이 없는 멤버입니다.")
            }
    )
    ResponseEntity<RecipeInfoResponseDto> updateRecipe(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody RecipeRequestDto recipeRequestDto, @PathVariable Long recipeId);

    @Operation(
            summary = "레시피 삭제",
            description = "본인이 작성한 레시피를 삭제합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "레시피 삭제 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RecipeInfoResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "레시피를 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "403", description = "레시피에 접근할 권한이 없는 멤버입니다.")
            }
    )
    ResponseEntity<Void> deleteRecipe(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long recipeId);
}
