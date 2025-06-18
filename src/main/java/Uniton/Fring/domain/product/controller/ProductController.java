package Uniton.Fring.domain.product.controller;

import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.domain.product.service.ProductService;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController implements ProductApiSpecification {

    private final ProductService productService;

    // 상품 조회

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

    // 상품 추가

    // 상품 수정

    // 상품 삭제
}
