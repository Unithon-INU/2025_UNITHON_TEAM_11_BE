package Uniton.Fring.domain.product.controller;

import Uniton.Fring.domain.product.dto.req.AddProductRequestDto;
import Uniton.Fring.domain.product.dto.req.UpdateProductRequestDto;
import Uniton.Fring.domain.product.dto.res.ProductInfoResponseDto;
import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Product", description = "상품 관련 API")
public interface ProductApiSpecification {

    @Operation(
            summary = "상품 검색",
            description = "상품 검색 기능 (10개)<br><br>키워드를 포함한 상품 정보들을 반환합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "상품 검색 응답 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleProductResponseDto.class, type = "array")
                            )
                    )
            }
    )
    ResponseEntity<List<SimpleProductResponseDto>> searchProduct(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page);

    @Operation(
            summary = "농수산 상세 정보 조회",
            description = "농수산 상품 정보를 상세 조회합니다. <br>리뷰는 기본적으로 좋아요 순으로 3개가 반환됩니다. <br>(최신순 정렬은 &sort=createdAt,desc 사용)",
            parameters = @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "농수산 상세 정보 응답 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProductInfoResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없습니다."),

            }
    )
    ResponseEntity<ProductInfoResponseDto> getProduct(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long productId, @RequestParam(defaultValue = "0") int page);

    @Operation(
            summary = "특가 농수산 더보기 조회",
            description = "농수산 상품을 할인율을 기준으로 정렬해 반환합니다. (10개)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "특가 농수산 더보기 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleProductResponseDto.class, type = "array")
                            )
                    )
            }
    )
    ResponseEntity<List<SimpleProductResponseDto>> getSaleProductList(@AuthenticationPrincipal UserDetailsImpl userDetails);

    @Operation(
            summary = "추천 농수산 더보기 조회",
            description = "농수산 상품을 평점을 기준으로 정렬해 반환합니다. (10개)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "추천 농수산 더보기 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleProductResponseDto.class, type = "array")
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
                            description = "자주 구매한 농수산 더보기 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleProductResponseDto.class, type = "array")
                            )
                    )
            }
    )
    ResponseEntity<List<SimpleProductResponseDto>> getFrequentProductList(@AuthenticationPrincipal UserDetailsImpl userDetails);

    @Operation(
            summary = "농수산품 추가",
            description = "농수산 상품을 추가합니다. \n 첫 번째 배열의 사진이 상품의 메인 이미지로 선택됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "농수산품 추가 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProductInfoResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "파일 변환에 실패했습니다.")
            }
    )
    ResponseEntity<ProductInfoResponseDto> addProduct(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                      @RequestPart @Valid AddProductRequestDto addProductRequestDto,
                                                      @RequestPart("mainImage") MultipartFile mainImage);

    @Operation(
            summary = "농수산품 수정",
            description = "본인이 올린 농수산 상품 정보를 수정합니다. <br>첫 번째 배열의 사진이 상품의 메인 이미지로 선택됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "농수산품 수정 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProductInfoResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "파일 변환에 실패했습니다."),
                    @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "403", description = "상품에 접근할 권한이 없는 회원입니다.")

            }
    )
    ResponseEntity<ProductInfoResponseDto> updateProduct(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long productId,
                                                         @RequestPart @Valid UpdateProductRequestDto updateProductRequestDto,
                                                         @RequestPart("mainImage") MultipartFile mainImage,
                                                         @RequestPart("descriptionImages") List<MultipartFile> descriptionImages);

    @Operation(
            summary = "농수산품 삭제",
            description = "본인이 올린 농수산 상품을 삭제합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "농수산품 삭제 성공"
                    ),
                    @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "403", description = "상품에 접근할 권한이 없는 회원입니다.")
            }
    )
    ResponseEntity<Void> deleteProduct(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long productId);
}

