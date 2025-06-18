package Uniton.Fring.domain.product.controller;

import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@Tag(name = "Product", description = "상품 관련 API")
public interface ProductApiSpecification {

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

