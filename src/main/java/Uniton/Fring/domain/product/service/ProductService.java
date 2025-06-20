package Uniton.Fring.domain.product.service;

import Uniton.Fring.domain.member.dto.res.MemberInfoResponseDto;
import Uniton.Fring.domain.member.entity.Member;
import Uniton.Fring.domain.member.repository.MemberRepository;
import Uniton.Fring.domain.product.dto.res.ProductDescriptionResponseDto;
import Uniton.Fring.domain.product.dto.res.ProductInfoResponseDto;
import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.domain.product.entity.Product;
import Uniton.Fring.domain.product.repository.ProductRepository;
import Uniton.Fring.domain.purchase.Purchase;
import Uniton.Fring.domain.purchase.PurchaseRepository;
import Uniton.Fring.domain.review.Review;
import Uniton.Fring.domain.review.ReviewRepository;
import Uniton.Fring.domain.review.ReviewResponseDto;
import Uniton.Fring.global.exception.CustomException;
import Uniton.Fring.global.exception.ErrorCode;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final PurchaseRepository purchaseRepository;

    @Transactional(readOnly = true)
    public ProductInfoResponseDto getProduct(UserDetailsImpl userDetails, Long productId, int page) {

        log.info("[농수산 상세 조회 요청]");

        // 리뷰 페이지
        Pageable pageable = PageRequest.of(page, 3, Sort.by(Sort.Direction.DESC, "likeCount"));

        Long memberId;
        if (userDetails != null) { memberId = userDetails.getMember().getId(); } else {
            memberId = null;
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("[농수산 조회 실패] 농수산 없음: productId={}", productId);
                    return new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
                });

        Member member = memberRepository.findById(product.getMemberId())
                .orElseThrow(() -> {
                    log.warn("[회원 정보 조회 실패] 회원 없음: memberId={}", product.getMemberId());
                    return new CustomException(ErrorCode.MEMBER_NOT_FOUND);
                });

        // 작성자 정보
        MemberInfoResponseDto memberInfoResponseDto = MemberInfoResponseDto.fromMember(member);

        // 설명 DTO
        ProductDescriptionResponseDto productDescriptionResponseDto = ProductDescriptionResponseDto.builder().product(product).build();

        // 리뷰 페이지 조회
        Page<Review> reviewPage = reviewRepository.findByProductId(productId, pageable);

        // 리뷰에서 memberId만 추출하고 중복 제거하여 리스트 생성
        List<Long> memberIds = reviewPage.getContent().stream()
                .map(Review::getMemberId)
                .distinct()
                .collect(Collectors.toList());
        // 리뷰에 등장하는 멤버들을 한 번에 조회
        List<Member> members = memberRepository.findAllById(memberIds);
        // 멤버 리스트를 Map 형태로 변환 (멤버ID → 멤버 객체)
        Map<Long, Member> memberMap = members.stream()
                .collect(Collectors.toMap(Member::getId, Function.identity()));

        List<ReviewResponseDto> reviewResponseDtoList = reviewPage.getContent().stream()
                .map(review -> {
                    // 해당 리뷰 작성자 정보 가져오기 (memberMap에서 꺼냄)
                    Member reviewer = memberMap.get(review.getMemberId());
                    if (reviewer == null) {
                        throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
                    }

                    MemberInfoResponseDto reviewerInfoResponseDto = MemberInfoResponseDto.fromReviewer(reviewer);

                    // 구매 엔티티에서 멤버 + 상품으로 구매 내역 찾기 (최신 1건)
                    Optional<Purchase> purchaseOpt = purchaseRepository.findTopByMemberIdAndProductIdOrderByPurchaseDateDesc(
                            review.getMemberId(), review.getProductId());

                    // Optional 내부에 값이 있으면, 그 값을 인자로 받아서 특정 변환
                    String purchaseOption = purchaseOpt
                            .map(Purchase::getPurchaseOption)
                            .orElse("옵션 정보 없음");

                    return ReviewResponseDto.builder()
                            .review(review)
                            .memberInfo(reviewerInfoResponseDto)
                            .purchaseOption(purchaseOption).build();
                })
                .toList();

        int totalReviewCount = reviewRepository.countByProductId(productId);
        int totalImageCount = reviewRepository.countTotalImagesByProductId(productId);

        Pageable recentImagePageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Review> recentReviews = reviewRepository.findTop5ByProductIdOrderByCreatedAtDesc(productId);
        List<Long> reviewIds = recentReviews.stream()
                .map(Review::getId)
                .toList();
        List<String> recentImages = reviewRepository.findTopImageUrlsByReviewIds(reviewIds, recentImagePageable);

        log.info("[농수산 상세 조회 성공]");

        return ProductInfoResponseDto.builder()
                .product(product)
                .memberInfoResponseDto(memberInfoResponseDto)
                .productDescriptionResponseDto(productDescriptionResponseDto)
                .reviews(reviewResponseDtoList)
                .totalReviewCount(totalReviewCount)
                .totalImageCount(totalImageCount)
                .recentImageUrls(recentImages)
                .build();
    }

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

//    public ProductInfoResponseDto addProduct(UserDetailsImpl userDetails, AddProductRequestDto addProductRequestDto) {
//
//        log.info("[농수산품 추가 요청]");
//
//        Member member = userDetails.getMember();
//
//
//
//        log.info("[농수산품 추가 성공]");
//
//        return ProductInfoResponseDto
//    }
//
//    public ProductInfoResponseDto updateProduct(UserDetailsImpl userDetails, Long productId, UpdateProductRequestDto updateProductRequestDto) {
//
//        log.info("[농수산품 수정 요청]");
//
//        Member member = userDetails.getMember();
//
//        log.info("[농수산품 수정 성공]");
//
//        return ProductInfoResponseDto
//    }

    public void deleteProduct(UserDetailsImpl userDetails, Long productId) {

        log.info("[농수산품 삭제 요청]");

        Member member = userDetails.getMember();

        log.info("[농수산품 삭제 성공]");
    }
}
