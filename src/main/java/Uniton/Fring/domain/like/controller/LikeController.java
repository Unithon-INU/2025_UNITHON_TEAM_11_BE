package Uniton.Fring.domain.like.controller;

import Uniton.Fring.domain.like.dto.res.LikeStatusResponseDto;
import Uniton.Fring.domain.like.dto.res.LikedItemsResponseDto;
import Uniton.Fring.domain.like.service.LikeService;
import Uniton.Fring.domain.member.dto.res.SimpleMemberResponseDto;
import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.domain.recipe.dto.res.SimpleRecipeResponseDto;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    // 찜한 레시피
    @GetMapping("/recipe")
    public ResponseEntity<LikedItemsResponseDto<SimpleRecipeResponseDto>> getLikedRecipe(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                                         @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.status(HttpStatus.OK).body(likeService.getLikedRecipe(userDetails, page));
    }

    // 찜한 유저
    @GetMapping("/consumer")
    public ResponseEntity<LikedItemsResponseDto<SimpleMemberResponseDto>> getLikedMember(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                        @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.status(HttpStatus.OK).body(likeService.getLikedMember(userDetails, page));
    }

    // 찜한 농수산품
    @GetMapping("/product")
    public ResponseEntity<LikedItemsResponseDto<SimpleProductResponseDto>> getLikedProduct(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                          @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.status(HttpStatus.OK).body(likeService.getLikedProduct(userDetails, page));
    }

    // 찜한 스토어팜
    @GetMapping("/farmer")
    public ResponseEntity<LikedItemsResponseDto<SimpleMemberResponseDto>> getLikedFarmer(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.status(HttpStatus.OK).body(likeService.getLikedFarmer(userDetails, page));
    }
}
