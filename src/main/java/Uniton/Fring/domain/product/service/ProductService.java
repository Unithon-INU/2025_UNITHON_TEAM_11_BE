package Uniton.Fring.domain.product.service;

import Uniton.Fring.domain.client.AiClientService;
import Uniton.Fring.domain.like.repository.MemberLikeRepository;
import Uniton.Fring.domain.like.repository.ProductLikeRepository;
import Uniton.Fring.domain.like.repository.ReviewLikeRepository;
import Uniton.Fring.domain.member.dto.res.MemberInfoResponseDto;
import Uniton.Fring.domain.member.entity.Member;
import Uniton.Fring.domain.member.repository.MemberRepository;
import Uniton.Fring.domain.member.service.MypageService;
import Uniton.Fring.domain.product.dto.req.AddProductRequestDto;
import Uniton.Fring.domain.product.dto.req.UpdateProductRequestDto;
import Uniton.Fring.domain.product.dto.res.ProductInfoResponseDto;
import Uniton.Fring.domain.product.dto.res.ProductOptionResponseDto;
import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.domain.product.entity.Product;
import Uniton.Fring.domain.product.entity.ProductOption;
import Uniton.Fring.domain.product.repository.ProductOptionRepository;
import Uniton.Fring.domain.product.repository.ProductRepository;
import Uniton.Fring.domain.purchase.repository.PurchaseRepository;
import Uniton.Fring.domain.review.dto.res.ReviewResponseDto;
import Uniton.Fring.domain.review.entity.Review;
import Uniton.Fring.domain.review.repository.ReviewRepository;
import Uniton.Fring.global.exception.CustomException;
import Uniton.Fring.global.exception.ErrorCode;
import Uniton.Fring.global.s3.S3Service;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductLikeRepository productLikeRepository;
    private final ProductOptionRepository productOptionRepository;
    private final MemberRepository memberRepository;
    private final MemberLikeRepository memberLikeRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final PurchaseRepository purchaseRepository;
    private final S3Service s3Service;
    private final MypageService mypageService;
    private final AiClientService aiClientService;

    @Transactional(readOnly = true)
    public ProductInfoResponseDto getProduct(UserDetailsImpl userDetails, Long productId, int page) {

        log.info("[농수산 상세 조회 요청]");

        // 리뷰 페이지
        Pageable pageable = PageRequest.of(page, 3, Sort.by(Sort.Direction.DESC, "likeCount"));

        Long memberId;
        if (userDetails != null) {
            memberId = userDetails.getMember().getId();
            mypageService.saveOrUpdate(memberId, productId);
        } else {
            memberId = null;
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("[농수산 조회 실패] 농수산 없음: productId={}", productId);
                    return new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
                });

        // 옵션 리스트 조회
        List<ProductOption> productOptions = productOptionRepository.findByProductId(productId);
        List<ProductOptionResponseDto> productOptionResponseDtos = productOptions.stream()
                .map(productOption ->  ProductOptionResponseDto.builder().product(product).productOption(productOption).build())
                .toList();

        Member member = memberRepository.findById(product.getMemberId())
                .orElseThrow(() -> {
                    log.warn("[회원 정보 조회 실패] 회원 없음: memberId={}", product.getMemberId());
                    return new CustomException(ErrorCode.MEMBER_NOT_FOUND);
                });

        Boolean isLikedProduct = null;
        if (memberId != null) {
            isLikedProduct = productLikeRepository.existsByMemberIdAndProductId(memberId, product.getId());
        }

        Boolean isLikedMember = null;
        if (memberId != null) {
            isLikedMember = memberLikeRepository.existsByMemberIdAndLikedMemberId(memberId, member.getId());
        }

        // 작성자 정보
        MemberInfoResponseDto memberInfoResponseDto = MemberInfoResponseDto.fromMember(member, isLikedMember);
        log.info("[작성자 정보 응답 생성 완료]");

        // 리뷰 페이지 조회
        Page<Review> reviewPage = reviewRepository.findByProductId(productId, pageable);

        // 리뷰에서 memberId만 추출하고 중복 제거하여 리스트 생성
        List<Long> reviewMemberIds = reviewPage.getContent().stream()
                .map(Review::getMemberId)
                .distinct()
                .collect(Collectors.toList());
        // 리뷰에 등장하는 멤버들을 한 번에 조회
        List<Member> reviewMembers = memberRepository.findAllById(reviewMemberIds);
        // 멤버 리스트를 Map 형태로 변환 (멤버ID → 멤버 객체)
        Map<Long, Member> reviewMemberMap = reviewMembers.stream()
                .collect(Collectors.toMap(Member::getId, Function.identity()));

        List<ReviewResponseDto> reviewResponseDtoList = reviewPage.getContent().stream()
                .map(review -> {
                    // 해당 리뷰 작성자 정보 가져오기 (memberMap에서 꺼냄)
                    Member reviewer = reviewMemberMap.get(review.getMemberId());
                    if (reviewer == null) {
                        throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
                    }

                    Boolean isLikedReviewer = null;
                    if (memberId != null) {
                        isLikedReviewer = memberLikeRepository.existsByMemberIdAndLikedMemberId(memberId, reviewer.getId());
                    }

                    MemberInfoResponseDto reviewerInfoResponseDto = MemberInfoResponseDto.fromReviewer(reviewer, isLikedReviewer);

                    Boolean isLikedReview = null;
                    if (memberId != null) {
                        isLikedReview = reviewLikeRepository.existsByMemberIdAndReviewId(memberId, review.getId());
                    }

                    return ReviewResponseDto.builder()
                            .review(review)
                            .memberInfo(reviewerInfoResponseDto)
                            .isLiked(isLikedReview)
                            .purchaseOption(review.getPurchaseOption())
                            .build();
                })
                .toList();
        log.info("[리뷰 정보 응답 생성 완료]");

        int totalReviewCount = reviewRepository.countByProductId(productId);
        int totalImageCount = reviewRepository.countTotalImagesByProductId(productId);

        Pageable recentImagePageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Review> recentReviews = reviewRepository.findTop5ByProductIdOrderByCreatedAtDesc(productId);
        List<Long> reviewIds = recentReviews.stream()
                .map(Review::getId)
                .toList();
        List<String> recentImages = reviewRepository.findTopImageUrlsByReviewIds(reviewIds, recentImagePageable);
        log.info("[최근 리뷰 정보 조회 완료]");

        List<Product> bestProducts = productRepository.findTop5ByOrderByLikeCountDesc();
        List<SimpleProductResponseDto> bestProductResponseDtos = bestProducts.stream()
                .map(bestProduct -> {
                    Boolean isLikedBestProduct = null;

                    if (memberId != null) {
                        isLikedBestProduct = productLikeRepository.existsByMemberIdAndProductId(memberId, bestProduct.getId());
                    }

                    return SimpleProductResponseDto.builder().product(bestProduct).isLiked(isLikedBestProduct).build();
                }).toList();

        List<SimpleProductResponseDto> relatedProducts = aiClientService.findCachedRelatedProducts(userDetails, productId);

        log.info("[농수산 상세 조회 성공]");

        return ProductInfoResponseDto.builder()
                .product(product)
                .memberInfoResponseDto(memberInfoResponseDto)
                .isLiked(isLikedProduct)
                .reviews(reviewResponseDtoList)
                .totalReviewCount(totalReviewCount)
                .totalImageCount(totalImageCount)
                .recentImageUrls(recentImages)
                .bestProducts(bestProductResponseDtos)
                .relatedProducts(relatedProducts)
                .productOptions(productOptionResponseDtos)
                .build();
    }

    @Transactional(readOnly = true)
    public List<SimpleProductResponseDto> getSaleProductList(UserDetailsImpl userDetails) {

        log.info("[특가 농수산 더보기 조회 요청]");

        Long memberId;
        if (userDetails != null) { memberId = userDetails.getMember().getId(); } else {
            memberId = null;
        }

        List<Product> saleProducts = productRepository.findTop10ByOrderByDiscountRateDesc();

        List<SimpleProductResponseDto> saleProductResponseDtos = saleProducts.stream()
                .map(product -> {
                    Boolean isLikedProduct = null;

                    if (memberId != null) {
                        isLikedProduct = productLikeRepository.existsByMemberIdAndProductId(memberId, product.getId());
                    }

                    return SimpleProductResponseDto.builder().product(product).isLiked(isLikedProduct).build();
                }).toList();

        log.info("[특가 농수산 더보기 조회 성공]");

        return saleProductResponseDtos;
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
                        isLikedProduct = productLikeRepository.existsByMemberIdAndProductId(memberId, product.getId());
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
                        isLikedProduct = productLikeRepository.existsByMemberIdAndProductId(memberId, product.getId());
                    }

                    return SimpleProductResponseDto.builder().product(product).isLiked(isLikedProduct).build();
                }).toList();

        log.info("[자주 구매한 농수산품 더보기 조회 성공]");

        return frequentProductResponseDtos;
    }

    @Transactional
    public ProductInfoResponseDto addProduct(UserDetailsImpl userDetails, AddProductRequestDto addProductRequestDto,
                                             MultipartFile mainImage, List<MultipartFile> descriptionImages) {

        log.info("[농수산품 추가 요청]");

        Pair<String, List<String>> imageData = s3Service.uploadMainAndDescriptionImages(
                mainImage, descriptionImages,
                "products", "productDescriptions");

        String mainImageUrl = imageData.getFirst();
        List<String> productDescriptionImages = imageData.getSecond();

        Product product = new Product(userDetails.getMember().getId(),addProductRequestDto , mainImageUrl, productDescriptionImages);
        productRepository.save(product);

        List<ProductOptionResponseDto> productOptionResponseDtos = new ArrayList<>();
        // 옵션 저장
        if (addProductRequestDto.getProductOptions() != null && !addProductRequestDto.getProductOptions().isEmpty()) {
            List<ProductOption> productOptions = addProductRequestDto.getProductOptions().stream()
                    .map(optionDto -> new ProductOption(product.getId(), optionDto.getOptionName(), optionDto.getAdditionalPrice()))
                    .toList();
            productOptionRepository.saveAll(productOptions);
            log.info("[옵션 {}개 추가 완료]", productOptions.size());

            productOptionResponseDtos = productOptions.stream()
                    .map(productOption ->  ProductOptionResponseDto.builder().product(product).productOption(productOption).build())
                    .toList();
        }

        aiClientService.relatedProducts(userDetails, product.getId());

        MemberInfoResponseDto memberInfoResponseDto = MemberInfoResponseDto.fromMember(userDetails.getMember(), null);

        log.info("[농수산품 추가 성공]");

        return ProductInfoResponseDto.builder()
                .product(product)
                .memberInfoResponseDto(memberInfoResponseDto)
                .isLiked(null)
                .reviews(new ArrayList<>())
                .totalReviewCount(0)
                .totalImageCount(0)
                .recentImageUrls(new ArrayList<>())
                .productOptions(productOptionResponseDtos)
                .bestProducts(new ArrayList<>())
                .build();
    }

    @Transactional
    public ProductInfoResponseDto updateProduct(UserDetailsImpl userDetails, Long productId, UpdateProductRequestDto updateProductRequestDto,
                                                MultipartFile mainImage, List<MultipartFile> descriptionImages) {

        log.info("[농수산품 수정 요청]");

        Member member = userDetails.getMember();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("[농수산품 수정 실패] 상품 없음: productId={}", productId);
                    return new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
                });

        if (!product.getMemberId().equals(member.getId())) {
            log.warn("[농수산품 수정 실패] 사용자 권한 없음: memberId={}, productOwnerId={}", member.getId(), product.getMemberId());
            throw new CustomException(ErrorCode.PRODUCT_MEMBER_NOT_MATCH);
        }

        // 기존 이미지 보관
        String oldMainImage = product.getMainImageUrl();
        List<String> oldDescriptionImages = product.getDescriptionImageUrl();

        Pair<String, List<String>> imageData = s3Service.uploadMainAndDescriptionImages(
                mainImage, descriptionImages,
                "products", "productDescriptions");

        String mainImageUrl = imageData.getFirst();
        List<String> productDescriptionImages = imageData.getSecond();

        // 상품 정보 수정
        product.updateProduct(updateProductRequestDto , mainImageUrl, productDescriptionImages);

        // 기존 옵션 제거
        productOptionRepository.deleteByProductId(productId);
        log.info("[상품 옵션 삭제 완료] productId={}", productId);

        // 새 옵션 추가
        List<ProductOptionResponseDto> productOptionResponseDtos = new ArrayList<>();
        if (updateProductRequestDto.getOptions() != null && !updateProductRequestDto.getOptions().isEmpty()) {
            List<ProductOption> productOptions = updateProductRequestDto.getOptions().stream()
                    .map(optionDto -> new ProductOption(productId, optionDto.getOptionName(), optionDto.getAdditionalPrice()))
                    .toList();
            productOptionRepository.saveAll(productOptions);
            log.info("[상품 옵션 새로 저장 완료] count={}", productOptions.size());

            productOptionResponseDtos = productOptions.stream()
                    .map(option -> ProductOptionResponseDto.builder().product(product).productOption(option).build())
                    .toList();
        }

        if (oldMainImage != null && !oldMainImage.isBlank()) {
            s3Service.delete(oldMainImage);
        }
        if (oldDescriptionImages != null && !oldDescriptionImages.isEmpty()) {
            for (String url : oldDescriptionImages) {
                if (url != null && !url.isBlank()) {
                    s3Service.delete(url);
                }
            }
        }

        log.info("[농수산품 수정 성공]");

        return ProductInfoResponseDto.builder()
                .product(product)
                .memberInfoResponseDto(MemberInfoResponseDto.fromMember(member, null))
                .isLiked(null)
                .reviews(new ArrayList<>())
                .totalReviewCount(0)
                .totalImageCount(0)
                .recentImageUrls(new ArrayList<>())
                .productOptions(productOptionResponseDtos)
                .bestProducts(new ArrayList<>())
                .build();
    }

    @Transactional
    public void deleteProduct(UserDetailsImpl userDetails, Long productId) {

        log.info("[농수산품 삭제 요청]");

        Member member = userDetails.getMember();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("[농수산품 삭제 실패] 상품 없음: productId={}", productId);
                    return new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
                });

        if (!product.getMemberId().equals(member.getId())) {
            log.warn("[농수산품 삭제 실패] 사용자 권한 없음: memberId={}, productOwnerId={}", member.getId(), product.getMemberId());
            throw new CustomException(ErrorCode.PRODUCT_MEMBER_NOT_MATCH);
        }

        String mainImage = product.getMainImageUrl();
        List<String> descriptionImages = product.getDescriptionImageUrl();

        productRepository.delete(product);

        log.info("[농수산품 삭제 성공]");

        if (mainImage != null && !mainImage.isBlank()) {
            s3Service.delete(mainImage);
        }
        if (descriptionImages != null && !descriptionImages.isEmpty()) {
            for (String url : descriptionImages) {
                if (url != null && !url.isBlank()) {
                    s3Service.delete(url);
                }
            }
        }
    }
}
