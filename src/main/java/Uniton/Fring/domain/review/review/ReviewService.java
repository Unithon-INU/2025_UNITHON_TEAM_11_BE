package Uniton.Fring.domain.review.review;

import Uniton.Fring.domain.member.dto.res.MemberInfoResponseDto;
import Uniton.Fring.domain.review.dto.req.ProductReviewRequestDto;
import Uniton.Fring.domain.review.dto.req.RecipeReviewRequestDto;
import Uniton.Fring.domain.review.dto.res.ReviewResponseDto;
import Uniton.Fring.domain.review.entity.Review;
import Uniton.Fring.domain.review.repository.ReviewRepository;
import Uniton.Fring.global.exception.CustomException;
import Uniton.Fring.global.exception.ErrorCode;
import Uniton.Fring.global.s3.S3Service;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final S3Service s3Service;

    @Transactional
    public ReviewResponseDto addProductReview(UserDetailsImpl userDetails,
                                              ProductReviewRequestDto productReviewRequestDto,
                                              List<MultipartFile> images) {

        log.info("[상품 리뷰 등록 요청]");

        List<String> imageUrls = uploadReviewImages(images);

        Review review = Review.fromProductReview(userDetails.getMember().getId(), productReviewRequestDto, imageUrls, productReviewRequestDto.getPurchase_option());

        reviewRepository.save(review);

        MemberInfoResponseDto memberInfoResponseDto = MemberInfoResponseDto.fromMember(userDetails.getMember(), null);

        log.info("[상품 리뷰 등록 성공]");

        return ReviewResponseDto.builder()
                .memberInfo(memberInfoResponseDto)
                .review(review)
                .purchaseOption(productReviewRequestDto.getPurchase_option())
                .build();
    }

    @Transactional
    public ReviewResponseDto addRecipeReview(UserDetailsImpl userDetails,
                                             RecipeReviewRequestDto recipeReviewRequestDto,
                                             List<MultipartFile> images) {

        log.info("[레시피 리뷰 등록 요청]");

        // 이미 레시피 리뷰가 존재하는지 확인
        boolean alreadyReviewed = reviewRepository.existsByMemberIdAndRecipeId(userDetails.getMember().getId(), recipeReviewRequestDto.getRecipeId());
        if (alreadyReviewed) {
            throw new CustomException(ErrorCode.ALREADY_REVIEWED);
        }

        List<String> imageUrls = uploadReviewImages(images);

        Review review = Review.fromRecipeReview(userDetails.getMember().getId(), recipeReviewRequestDto, imageUrls);

        reviewRepository.save(review);

        MemberInfoResponseDto memberInfoResponseDto = MemberInfoResponseDto.fromMember(userDetails.getMember(), null);

        log.info("[레시피 리뷰 등록 성공]");

        return ReviewResponseDto.builder()
                .memberInfo(memberInfoResponseDto)
                .review(review)
                .purchaseOption(null)
                .build();
    }

    private List<String> uploadReviewImages(List<MultipartFile> images) {
        List<String> imageUrls = new ArrayList<>();

        if (images != null && !images.isEmpty()) {
            try {
                for (int i = 0; i < images.size(); i++) {
                    String url = s3Service.upload(images.get(i), "reviews");
                    imageUrls.add(url);
                }
            } catch (IOException e) {
                throw new CustomException(ErrorCode.FILE_CONVERT_FAIL);
            }
        }

        log.info("총 {}개의 리뷰 이미지 업로드 완료", imageUrls.size());

        return imageUrls;
    }
}
