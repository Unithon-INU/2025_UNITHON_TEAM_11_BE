package Uniton.Fring.domain.like.controller;

import Uniton.Fring.domain.like.dto.res.LikeStatusResponseDto;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

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


}
