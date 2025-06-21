package Uniton.Fring.domain.review.controller;

import Uniton.Fring.domain.review.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController implements ReviewApiSpecification {

    private final ReviewService reviewService;

    // 상품 리뷰 생성
//    @PostMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<ReviewResponseDto> addProductReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
//                                                              @PathVariable Long productId,
//                                                              @RequestPart @Valid RecipeReviewRequestDto recipeReviewRequestDto,
//                                                              @RequestPart(value = "images", required = true) List<MultipartFile> images) {
//        return ResponseEntity.status(HttpStatus.OK).body(reviewService.addProductReview(userDetails, productId, recipeReviewRequestDto, images));
//    }
//
//    // 레시피 리뷰 생성
//    @PostMapping(value = "/{recipeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<ReviewResponseDto> addRecipeReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
//                                                           @PathVariable Long recipeId,
//                                                           @RequestPart @Valid RecipeReviewRequestDto recipeReviewRequestDto,
//                                                           @RequestPart(value = "images", required = true) List<MultipartFile> images) {
//        return ResponseEntity.status(HttpStatus.OK).body(reviewService.addRecipeReview(userDetails, recipeId, recipeReviewRequestDto, images));
//    }
}
