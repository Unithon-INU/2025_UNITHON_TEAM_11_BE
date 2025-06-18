package Uniton.Fring.domain.product.service;

import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.domain.product.entity.Product;
import Uniton.Fring.domain.product.repository.ProductRepository;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;



    @Transactional(readOnly = true)
    public List<SimpleProductResponseDto> getBestProductList(UserDetailsImpl userDetails) {

        log.info("[추천 농수산 더보기 조회 요청]");

        Long memberId;
        if (userDetails != null) { memberId = userDetails.getMember().getId(); } else {
            memberId = null;
        }

        List<Product> bestProducts = productRepository.findTop10ByOrderByRatingDesc();

        List<SimpleProductResponseDto> bestProductResponseDtos = bestProducts.stream()
                .map(product -> {
                    Boolean isLikedProduct = null;

                    if (memberId != null) {
                        isLikedProduct = productRepository.existsByMemberIdAndId(memberId, product.getId());
                    }

                    return SimpleProductResponseDto.builder().product(product).isLiked(isLikedProduct).build();
                }).toList();

        log.info("[추천 농수산 더보기 조회 성공]");

        return bestProductResponseDtos;
    }

    @Transactional(readOnly = true)
    public List<SimpleProductResponseDto> getFrequentProductList(UserDetailsImpl userDetails) {


        log.info("[자주 구매한 농수산품 더보기 조회 요청]");

        Long memberId;
        if (userDetails != null) { memberId = userDetails.getMember().getId(); } else {
            memberId = null;
        }

        List<Product> frequentProducts = productRepository.findTopProductsByReviewCount(PageRequest.of(0, 10));

        List<SimpleProductResponseDto> frequentProductResponseDtos = frequentProducts.stream()
                .map(product -> {
                    Boolean isLikedProduct = null;

                    if (memberId != null) {
                        isLikedProduct = productRepository.existsByMemberIdAndId(memberId, product.getId());
                    }

                    return SimpleProductResponseDto.builder().product(product).isLiked(isLikedProduct).build();
                }).toList();

        log.info("[자주 구매한 농수산품 더보기 조회 성공]");

        return frequentProductResponseDtos;
    }


}
