package Uniton.Fring.domain.like.controller;

import Uniton.Fring.domain.like.dto.res.LikeStatusResponseDto;
import Uniton.Fring.domain.like.dto.res.LikedItemsResponseDto;
import Uniton.Fring.domain.member.dto.res.SimpleMemberResponseDto;
import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.domain.recipe.dto.res.SimpleRecipeResponseDto;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Like", description = "좋아요 관련 API")
public interface LikeApiSpecification {

    @Operation(
            summary = "회원 좋아요",
            description = "특정 회원에 좋아요 기능을 수행합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원 좋아요 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LikeStatusResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없습니다.")
            }
    )
    ResponseEntity<LikeStatusResponseDto> likeMember(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @PathVariable Long memberId);

    @Operation(
            summary = "상품 좋아요",
            description = "특정 상품에 좋아요 기능을 수행합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "상품 좋아요 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LikeStatusResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없습니다.")
            }
    )
    ResponseEntity<LikeStatusResponseDto> likeProduct(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                      @PathVariable Long productId);

    @Operation(
            summary = "레시피 좋아요",
            description = "특정 레시피에 좋아요 기능을 수행합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "레시피 좋아요 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LikeStatusResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "레시피를 찾을 수 없습니다.")
            }
    )
    ResponseEntity<LikeStatusResponseDto> likeRecipe(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @PathVariable Long recipeId);

    @Operation(
            summary = "리뷰 좋아요",
            description = "특정 리뷰에 좋아요 기능을 수행합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "리뷰 좋아요 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LikeStatusResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없습니다.")
            }
    )
    ResponseEntity<LikeStatusResponseDto> likeReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @PathVariable Long reviewId);

    @Operation(
            summary = "찜한 레시피 조회",
            description = "회원 본인의 찜한 레시피 목록을 조회합니다. (최신순 6개)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "찜한 레시피 목록 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LikedItemsResponseDto.class)
                            )
                    )
            }
    )
    ResponseEntity<LikedItemsResponseDto<SimpleRecipeResponseDto>> getLikedRecipe(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                                  @RequestParam(defaultValue = "0") int page);

    @Operation(
            summary = "찜한 유저 조회",
            description = "회원 본인의 찜한 유저 목록을 조회합니다. (최신순 8개, 소비자 유저만 포함)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "찜한 유저 목록 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LikedItemsResponseDto.class)
                            )
                    )
            }
    )
    ResponseEntity<LikedItemsResponseDto<SimpleMemberResponseDto>> getLikedMember(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                                  @RequestParam(defaultValue = "0") int page);

    @Operation(
            summary = "찜한 농수산품 조회",
            description = "회원 본인의 찜한 농수산품 목록을 조회합니다. (최신순 6개)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "찜한 농수산품 목록 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LikedItemsResponseDto.class)
                            )
                    )
            }
    )
    ResponseEntity<LikedItemsResponseDto<SimpleProductResponseDto>> getLikedProduct(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                                    @RequestParam(defaultValue = "0") int page);

    @Operation(
            summary = "찜한 스토어팜 조회",
            description = "회원 본인의 찜한 농부 유저 목록을 조회합니다. (최신순 8개, 생산자 유저만 포함)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "찜한 농부 유저 목록 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LikedItemsResponseDto.class)
                            )
                    )
            }
    )
    ResponseEntity<LikedItemsResponseDto<SimpleMemberResponseDto>> getLikedFarmer(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                                  @RequestParam(defaultValue = "0") int page);
}
