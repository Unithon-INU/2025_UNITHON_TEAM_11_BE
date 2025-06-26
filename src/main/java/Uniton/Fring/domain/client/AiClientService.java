package Uniton.Fring.domain.client;

import Uniton.Fring.domain.client.dto.req.RelatedProductsRequestDto;
import Uniton.Fring.domain.client.dto.req.TitleSuggestionRequestDto;
import Uniton.Fring.domain.like.entity.ProductLike;
import Uniton.Fring.domain.like.repository.ProductLikeRepository;
import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.domain.product.entity.Product;
import Uniton.Fring.domain.product.repository.ProductRepository;
import Uniton.Fring.global.exception.CustomException;
import Uniton.Fring.global.exception.ErrorCode;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiClientService {

    @Value("${backend.base-url}")
    private String backendUrl;

    private final AiClient aiClient;
    private final ProductRepository productRepository;
    private final ProductLikeRepository productLikeRepository;
    private final RecommendationRepository recommendationRepository;

    // 연관 상품 조회
    public List<SimpleProductResponseDto> relatedProducts(UserDetailsImpl userDetails, Long productId) {

        log.info("[연관 상품 조회 요청] 상품: {}", productId);

        Long loginMemberId;
        if (userDetails != null) {
            loginMemberId = userDetails.getMember().getId();
        } else {
            loginMemberId = null;
        }

        // 캐시 or AI 호출
        List<Product> related = fetchOrLoad(productId);

        // 좋아요 한꺼번에 조회
        List<Long> ids = related.stream().map(Product::getId).toList();
        Set<Long> likedIds = loginMemberId == null ? Set.of()
                : productLikeRepository.findByMemberIdAndProductIdIn(loginMemberId, ids)
                .stream()
                .map(ProductLike::getProductId)
                .collect(Collectors.toSet());

        log.info("[연관 상품 조회 성공] 상품: {}", productId);

        // DTO 변환
        return related.stream()
                .map(p -> SimpleProductResponseDto.builder()
                        .product(p)
                        .isLiked(likedIds.contains(p.getId()))
                        .build())
                .toList();
    }

    // 제목 추천
    public String suggestTitle(TitleSuggestionRequestDto titleSuggestionRequestDto) {

        log.info("[상품 제목 추천 요청]");
        return aiClient.suggestTitle(titleSuggestionRequestDto);
    }

    // 캐싱
    private List<Product> fetchOrLoad(Long productId) {

        // recommendation 테이블 확인
        List<Recommendation> cached = recommendationRepository.findByProductIdOrderByRankOrderAsc(productId);

        List<Long> ids;
        if (!cached.isEmpty()) {
            ids = cached.stream().map(Recommendation::getRelatedId).toList();
        } else {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> {
                        log.warn("[농수산 조회 실패] 농수산 없음: productId={}", productId);
                        return new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
                    });

            RelatedProductsRequestDto relatedProductsRequestDto = RelatedProductsRequestDto.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .build();

            // AI 호출
            ids = aiClient.relatedProducts(relatedProductsRequestDto);

            // 캐시 저장
            int rank = 1;
            for (Long id : ids) {
                recommendationRepository.save(Recommendation.builder().productId(productId).relatedId(id).rankOrder(rank++).build());
            }
        }

        if (ids.isEmpty()) return List.of();

        Map<Long, Product> productMap = productRepository.findAllById(ids)
                .stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        return ids.stream()
                .map(productMap::get)
                .filter(Objects::nonNull)
                .toList();
    }
}
