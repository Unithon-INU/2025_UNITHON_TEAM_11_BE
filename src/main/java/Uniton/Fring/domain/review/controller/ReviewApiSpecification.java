package Uniton.Fring.domain.review.controller;

import Uniton.Fring.domain.review.dto.req.ProductReviewRequestDto;
import Uniton.Fring.domain.review.dto.req.RecipeReviewRequestDto;
import Uniton.Fring.domain.review.dto.res.ReviewResponseDto;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Review", description = "리뷰 관련 API")
public interface ReviewApiSpecification {

    @Operation(
            summary = "농수산품 리뷰 등록",
            description = "농수산품에 대한 리뷰를 등록합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "농수산품 리뷰 등록 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReviewResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "파일 변환에 실패했습니다.")
            }
    )
    ResponseEntity<ReviewResponseDto> addProductReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                       @RequestPart @Valid ProductReviewRequestDto productReviewRequestDto,
                                                       @RequestPart(value = "images", required = false) List<MultipartFile> images);

    @Operation(
            summary = "레시피 리뷰 등록",
            description = "레시피 대한 리뷰를 등록합니다. <br>레시피에 대한 중복 리뷰는 불가능합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "레시피 리뷰 등록 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReviewResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "파일 변환에 실패했습니다.")
            }
    )
    ResponseEntity<ReviewResponseDto> addRecipeReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                      @RequestPart @Valid RecipeReviewRequestDto recipeReviewRequestDto,
                                                      @RequestPart(value = "images", required = false) List<MultipartFile> images);
}
