package Uniton.Fring.domain.client;

import Uniton.Fring.domain.client.dto.req.RelatedProductsRequestDto;
import Uniton.Fring.domain.client.dto.req.TitleSuggestionRequestDto;
import Uniton.Fring.domain.client.dto.res.ProductToChatbotReponseDto;
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
    @Transactional
    public List<SimpleProductResponseDto> relatedProducts(UserDetailsImpl userDetails, Long productId) {

        log.info("[연관 상품 조회 요청] 상품: {}", productId);

        Long loginMemberId = (userDetails != null) ? userDetails.getMember().getId() : null;

        // 상품 조회
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
        List<Long> ids = aiClient.relatedProducts(relatedProductsRequestDto);

        if (ids == null || ids.isEmpty()) {
            log.warn("[AI 호출 결과 없음] 추천 상품 리스트가 비어있음");
            return List.of();
        }

        log.info("[AI 호출 성공] productIds= {}", ids);

        // 캐시(Recommendation) 저장
        int rank = 1;
        for (Long id : ids) {
            // 자기 자신 제외
            if (id == null || id.equals(productId)) continue;
            // 이미 있음
            if (recommendationRepository.existsByProductIdAndRelatedId(productId,id)) continue;

            recommendationRepository.save(Recommendation.builder().productId(productId).relatedId(id).rankOrder(rank++).build());
        }

        // 상품 조회
        Map<Long, Product> productMap = productRepository.findAllById(ids)
                .stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        // 좋아요 정보 조회
        Set<Long> likedIds = loginMemberId == null ? Set.of()
                : productLikeRepository.findByMemberIdAndProductIdIn(loginMemberId, ids)
                .stream()
                .map(ProductLike::getProductId)
                .collect(Collectors.toSet());

        // DTO 변환 후 반환
        return ids.stream()
                .map(productMap::get)
                .filter(Objects::nonNull)
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

    @Transactional(readOnly = true)
    public List<SimpleProductResponseDto> findCachedRelatedProducts(UserDetailsImpl userDetails, Long productId) {

        log.info("[연관된 상품 캐시 조회 요청]");

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

        log.info("[연관된 상품 캐시 조회 성공]");

        return ids.stream()
                .map(map::get)
                .filter(Objects::nonNull)
                .map(p -> SimpleProductResponseDto.builder()
                        .product(p)
                        .isLiked(liked.contains(p.getId()))
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductToChatbotReponseDto getProductToChatBot(Long productId) {

        log.info("[챗봇 전달용 상품 정보 요청]");

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("[농수산 조회 실패] 농수산 없음: productId={}", productId);
                    return new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
                });

        log.info("[챗봇 전달용 상품 정보 성공]");

        return ProductToChatbotReponseDto.builder().product(product).build();
    }
}
