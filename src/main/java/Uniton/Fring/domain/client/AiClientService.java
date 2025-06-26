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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiClientService {

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

        log.info("[캐시 저장 요청]");

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

            log.info("[Ai 호출]");

            // AI 호출
            ids = aiClient.relatedProducts(relatedProductsRequestDto);

            if (ids == null || ids.isEmpty()) {
                log.warn("[AI 호출 결과 없음] 추천 상품 리스트가 비어있음, 캐시 저장 생략");
                return List.of();
            }

            log.info("[Ai 호출 성공, 결과 존재] productIds= {}", ids);

            // 캐시 저장
            Set<Long> already = new HashSet<>();
            int rank = 1;
            for (Long id : ids) {
                if (id == null || id.equals(productId) || !already.add(id)) continue;

                // 중복 저장 방지 - DB에 이미 같은 productId + relatedId 가 존재하는지 확인
                if (recommendationRepository.existsByProductIdAndRelatedId(productId, id)) {
                    continue;
                }

                recommendationRepository.save(
                        Recommendation.builder()
                                .productId(productId)
                                .relatedId(id)
                                .rankOrder(rank++)
                                .build());
            }

            log.info("[캐시 저장 완료]");
        }

        if (ids.isEmpty()) return List.of();

        Map<Long, Product> productMap = productRepository.findAllById(ids)
                .stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        log.info("[상품 조회 완료]");

        return ids.stream()
                .map(productMap::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SimpleProductResponseDto> findCachedRelatedProducts(UserDetailsImpl userDetails, Long productId) {
        List<Long> ids = recommendationRepository
                .findByProductIdOrderByRankOrderAsc(productId)
                .stream().map(Recommendation::getRelatedId).toList();

        if (ids.isEmpty()) return List.of();

        Map<Long, Product> map = productRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        Long memberId = userDetails == null ? null : userDetails.getMember().getId();
        Set<Long> liked = memberId == null ? Set.of()
                : productLikeRepository.findByMemberIdAndProductIdIn(memberId, ids).stream()
                .map(ProductLike::getProductId)
                .collect(Collectors.toSet());

        return ids.stream()
                .map(map::get)
                .filter(Objects::nonNull)
                .map(p -> SimpleProductResponseDto.builder()
                        .product(p)
                        .isLiked(liked.contains(p.getId()))
                        .build())
                .toList();
    }
}
