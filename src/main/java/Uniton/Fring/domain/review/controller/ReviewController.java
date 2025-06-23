package Uniton.Fring.domain.review.controller;

import Uniton.Fring.domain.review.dto.req.ProductReviewRequestDto;
import Uniton.Fring.domain.review.dto.req.RecipeReviewRequestDto;
import Uniton.Fring.domain.review.dto.res.ReviewResponseDto;
import Uniton.Fring.domain.review.service.ReviewService;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController implements ReviewApiSpecification {

    private final ReviewService reviewService;

    // 상품 리뷰 생성
    @PostMapping(value = "/product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponseDto> addProductReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                              @RequestPart @Valid ProductReviewRequestDto productReviewRequestDto,
                                                              @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.addProductReview(userDetails, productReviewRequestDto, images));
    }

    // 레시피 리뷰 생성
    @PostMapping(value = "/recipe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponseDto> addRecipeReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                           @RequestPart @Valid RecipeReviewRequestDto recipeReviewRequestDto,
                                                           @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.addRecipeReview(userDetails, recipeReviewRequestDto, images));
    }
}
