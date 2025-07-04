package Uniton.Fring.domain.recipe.controller;

import Uniton.Fring.domain.recipe.dto.req.RecipeRequestDto;
import Uniton.Fring.domain.recipe.dto.res.RecipeInfoResponseDto;
import Uniton.Fring.domain.recipe.dto.res.SimpleRecipeResponseDto;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Recipe", description = "레시피 관련 API")
public interface RecipeApiSpecification {

    @Operation(
            summary = "레시피 검색",
            description = "레시피 검색 기능 (10개)<br><br>키워드를 포함한 레시피 정보들을 반환합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "레시피 검색 응답 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleRecipeResponseDto.class, type = "array")
                            )
                    )
            }
    )
    ResponseEntity<List<SimpleRecipeResponseDto>> searchRecipe(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page);

    @Operation(
            summary = "레시피 상세 정보 조회",
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
    ResponseEntity<RecipeInfoResponseDto> getRecipe(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                    @PathVariable Long recipeId, @RequestParam(defaultValue = "0") int page);

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
            parameters = @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0"),
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
    public ResponseEntity<List<SimpleRecipeResponseDto>> getRecipeList(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam(defaultValue = "0") int page);

    @Operation(
            summary = "레시피 추가",
            description = "새로운 레시피를 추가합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "레시피 추가 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RecipeInfoResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "파일 변환에 실패했습니다.")
            }
    )
    ResponseEntity<RecipeInfoResponseDto> addRecipe(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                    @RequestPart @Valid RecipeRequestDto recipeRequestDto,
                                                    @RequestPart("mainImage") MultipartFile mainImage,
                                                    @RequestPart("descriptionImages") List<MultipartFile> descriptionImages);

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
                    @ApiResponse(responseCode = "500", description = "파일 변환에 실패했습니다."),
                    @ApiResponse(responseCode = "404", description = "레시피를 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "403", description = "레시피에 접근할 권한이 없는 멤버입니다.")
            }
    )
    ResponseEntity<RecipeInfoResponseDto> updateRecipe(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                       @PathVariable Long recipeId,
                                                       @RequestPart @Valid RecipeRequestDto recipeRequestDto,
                                                       @RequestPart("mainImage") MultipartFile mainImage,
                                                       @RequestPart("descriptionImages") List<MultipartFile> descriptionImages);

    @Operation(
            summary = "레시피 삭제",
            description = "본인이 작성한 레시피를 삭제합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "레시피 삭제 성공"
                    ),
                    @ApiResponse(responseCode = "404", description = "레시피를 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "403", description = "레시피에 접근할 권한이 없는 멤버입니다.")
            }
    )
    ResponseEntity<Void> deleteRecipe(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long recipeId);
}
