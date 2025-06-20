package Uniton.Fring.domain.product.controller;

import Uniton.Fring.domain.product.dto.res.ProductInfoResponseDto;
import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "Product", description = "상품 관련 API")
public interface ProductApiSpecification {

    @Operation(
            summary = "농수산 상세 정보 조회",
            description = "농수산 상품 정보를 상세 조회합니다. \n (기본값) 리뷰 정렬 기준: 3개, 좋아요 순 (최신순 정렬 시 &sort=createdAt,desc 파라미터 필요)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "농수산 상세 정보 응답 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProductInfoResponseDto.class)
                            )
                    )
            }
    )
    ResponseEntity<ProductInfoResponseDto> getProduct(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long productId, @PageableDefault(size = 3, sort = "likeCount", direction = Sort.Direction.DESC) Pageable pageable);

    @Operation(
            summary = "추천 농수산 더보기 조회",
            description = "농수산 상품을 평점을 기준으로 정렬해 반환합니다. (10개)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "메인 페이지 응답 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleProductResponseDto.class)
                            )
                    )
            }
    )
    ResponseEntity<List<SimpleProductResponseDto>> getBestProductList(@AuthenticationPrincipal UserDetailsImpl userDetails);

    @Operation(
            summary = "자주 구매한 농수산품 더보기 조회",
            description = "농수산 상품을 리뷰 수를 기준으로 정렬해 반환합니다. (10개)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "메인 페이지 응답 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleProductResponseDto.class)
                            )
                    )
            }
    )
    ResponseEntity<List<SimpleProductResponseDto>> getFrequentProductList(@AuthenticationPrincipal UserDetailsImpl userDetails);
}

