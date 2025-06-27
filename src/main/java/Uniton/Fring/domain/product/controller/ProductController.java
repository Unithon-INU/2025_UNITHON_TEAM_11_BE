package Uniton.Fring.domain.product.controller;

import Uniton.Fring.domain.product.dto.req.AddProductRequestDto;
import Uniton.Fring.domain.product.dto.req.UpdateProductRequestDto;
import Uniton.Fring.domain.product.dto.res.ProductInfoResponseDto;
import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.domain.product.service.ProductService;
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
@RequestMapping("/api/products")
public class ProductController implements ProductApiSpecification {

    private final ProductService productService;

    // 상품 검색
    @GetMapping("/search")
    public ResponseEntity<List<SimpleProductResponseDto>> searchProduct(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.searchProduct(userDetails, keyword, page));
    }

    // 농수산품 상세 정보 조회
    @GetMapping("/{productId}")
    public ResponseEntity<ProductInfoResponseDto> getProduct(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                             @PathVariable Long productId, @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProduct(userDetails, productId, page));
    }

    // 특가 농수산 더보기 조회
    @GetMapping("/sale")
    public ResponseEntity<List<SimpleProductResponseDto>> getSaleProductList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getSaleProductList(userDetails));
    }

    // 추천 농수산 더보기 조회
    @GetMapping("/best")
    public ResponseEntity<List<SimpleProductResponseDto>> getBestProductList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getBestProductList(userDetails));
    }

    // 자주 구매한 농수산품 더보기 조회
    @GetMapping("/frequent")
    public ResponseEntity<List<SimpleProductResponseDto>> getFrequentProductList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getFrequentProductList(userDetails));
    }

    // 농수산품 추가
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductInfoResponseDto> addProduct(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                             @RequestPart @Valid AddProductRequestDto addProductRequestDto,
                                                             @RequestPart("mainImage") MultipartFile mainImage,
                                                             @RequestPart("descriptionImages") List<MultipartFile> descriptionImages) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.addProduct(userDetails, addProductRequestDto, mainImage, descriptionImages));
    }

    // 농수산품 수정
    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductInfoResponseDto> updateProduct(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long productId,
                                                                @RequestPart @Valid UpdateProductRequestDto updateProductRequestDto,
                                                                @RequestPart("mainImage") MultipartFile mainImage,
                                                                @RequestPart("descriptionImages") List<MultipartFile> descriptionImages) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.updateProduct(userDetails, productId, updateProductRequestDto, mainImage, descriptionImages));
    }

    // 농수산품 삭제
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long productId) {
        productService.deleteProduct(userDetails, productId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
