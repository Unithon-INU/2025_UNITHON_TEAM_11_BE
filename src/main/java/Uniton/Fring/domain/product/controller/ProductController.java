package Uniton.Fring.domain.product.controller;

import Uniton.Fring.domain.product.dto.res.ProductInfoResponseDto;
import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.domain.product.service.ProductService;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController implements ProductApiSpecification {

    private final ProductService productService;

    // 농수산품상세 정보 조회
    @GetMapping("/{productId}")
    public ResponseEntity<ProductInfoResponseDto> getProduct(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long productId, @PageableDefault(size = 3, sort = "likeCount", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProduct(userDetails, productId, pageable));
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

//    // 농수산품 추가
//    @PostMapping
//    public ResponseEntity<ProductInfoResponseDto> addProduct(@AuthenticationPrincipal UserDetailsImpl userDetails, AddProductRequestDto addProductRequestDto) {
//        return ResponseEntity.status(HttpStatus.OK).body(productService.addProduct(userDetails, addProductRequestDto));
//    }
//
//    // 농수산품 수정
//    @PutMapping("/{productId}")
//    public ResponseEntity<ProductInfoResponseDto> updateProduct(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long productId, UpdateProductRequestDto updateProductRequestDto) {
//        return ResponseEntity.status(HttpStatus.OK).body(productService.updateProduct(userDetails, productId, updateProductRequestDto));
//    }
//
//    // 농수산품 삭제
//    @DeleteMapping("/{productId}")
//    public ResponseEntity<Void> deleteProduct(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long productId) {
//        productService.deleteProduct(userDetails, productId);
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//    }
}
