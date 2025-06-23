package Uniton.Fring.domain.like.controller;

import Uniton.Fring.domain.like.dto.res.LikeStatusResponseDto;
import Uniton.Fring.domain.like.service.LikeService;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class LikeController implements LikeApiSpecification{

    private final LikeService likeService;

    // 회원 좋아요
    @PostMapping("/member/{memberId}")
    public ResponseEntity<LikeStatusResponseDto> likeMember(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                            @PathVariable Long memberId) {
        return ResponseEntity.status(HttpStatus.OK).body(likeService.likeMember(userDetails, memberId));
    }

    // 상품 좋아요
    @PostMapping("/product/{productId}")
    public ResponseEntity<LikeStatusResponseDto> likeProduct(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                             @PathVariable Long productId) {
        return ResponseEntity.status(HttpStatus.OK).body(likeService.likeProduct(userDetails, productId));
    }

    // 레시피 좋아요
    @PostMapping("/recipe/{recipeId}")
    public ResponseEntity<LikeStatusResponseDto> likeRecipe(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                            @PathVariable Long recipeId) {
        return ResponseEntity.status(HttpStatus.OK).body(likeService.likeRecipe(userDetails, recipeId));
    }

    // 리뷰 좋아요
    @PostMapping("/review/{reviewId}")
    public ResponseEntity<LikeStatusResponseDto> likeReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                            @PathVariable Long reviewId) {
        return ResponseEntity.status(HttpStatus.OK).body(likeService.likeReview(userDetails, reviewId));
    }

    // 좋아요 목록 불러오기

}
