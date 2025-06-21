package Uniton.Fring.domain.review.review;

import Uniton.Fring.domain.review.repository.ReviewRepository;
import Uniton.Fring.global.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final S3Service s3Service;

//    public ReviewResponseDto addProductReview(UserDetailsImpl userDetails, Long productId,
//                                              RecipeReviewRequestDto recipeReviewRequestDto,
//                                              List<MultipartFile> images) {
//
//        log.info("[상품 리뷰 등록 요청]");
//
//        Pair<String, List<String>> imageData = s3Service.uploadMainAndStepImages(images, "reviews", "reviewSteps");
//        String mainImageUrl = imageData.getFirst();
//        List<String> descriptionImages = imageData.getSecond();
//
//        log.info("[상품 리뷰 등록 성공]");
//
//        return ;
//    }

//    public ReviewResponseDto addRecipeReview(UserDetailsImpl userDetails, Long recipeId,
//                                             RecipeReviewRequestDto recipeReviewRequestDto,
//                                             List<MultipartFile> images) {
//
//        log.info("[레시피 리뷰 등록 요청]");
//
//        Pair<String, List<String>> imageData = s3Service.uploadMainAndStepImages(images, "reviews", "reviewSteps");
//        String mainImageUrl = imageData.getFirst();
//        List<String> descriptionImages = imageData.getSecond();
//
//        log.info("[레시피 리뷰 등록 성공]");
//
//        return ;
//    }
}
