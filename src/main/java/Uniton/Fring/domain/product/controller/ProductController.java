package Uniton.Fring.domain.product.controller;

import Uniton.Fring.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;

    // 메인 페이지 상품 목록
//    @GetMapping
//    public ResponseEntity<Boolean> checkEmailDuplicated(@PathVariable String email) {
//        return ResponseEntity.status(HttpStatus.OK).body(memberService.checkEmailDuplicated(email));
//    }
}
