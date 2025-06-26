package Uniton.Fring.domain.member.service;

import Uniton.Fring.domain.like.entity.RecipeLike;
import Uniton.Fring.domain.like.repository.MemberLikeRepository;
import Uniton.Fring.domain.like.repository.ProductLikeRepository;
import Uniton.Fring.domain.like.repository.RecipeLikeRepository;
import Uniton.Fring.domain.member.dto.req.MypageRequestDto;
import Uniton.Fring.domain.member.dto.res.MypageDetailResponseDto;
import Uniton.Fring.domain.member.dto.res.MypageResponseDto;
import Uniton.Fring.domain.member.entity.Member;
import Uniton.Fring.domain.member.repository.MemberRepository;
import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.domain.product.entity.Product;
import Uniton.Fring.domain.product.entity.RecentProductView;
import Uniton.Fring.domain.product.repository.ProductRepository;
import Uniton.Fring.domain.product.repository.RecentProductViewRepository;
import Uniton.Fring.domain.purchase.dto.res.SimplePurchaseResponseDto;
import Uniton.Fring.domain.purchase.entity.Purchase;
import Uniton.Fring.domain.purchase.entity.PurchaseItem;
import Uniton.Fring.domain.purchase.repository.PurchaseItemRepository;
import Uniton.Fring.domain.purchase.repository.PurchaseRepository;
import Uniton.Fring.domain.recipe.dto.res.SimpleRecipeResponseDto;
import Uniton.Fring.domain.recipe.entity.Recipe;
import Uniton.Fring.domain.recipe.repository.RecipeRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MypageService {

    private final S3Service s3Service;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final ProductLikeRepository productLikeRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeLikeRepository recipeLikeRepository;
    private final ReviewRepository reviewRepository;
    private final RecentProductViewRepository recentProductViewRepository;
    private final PurchaseRepository purchaseRepository;
    private final PurchaseItemRepository purchaseItemRepository;
    private final MemberLikeRepository memberLikeRepository;

    @Transactional(readOnly = true)
    public MypageResponseDto getMypage(UserDetailsImpl userDetails) {

        log.info("[마이페이지 조회 요청]");

        MypageResponseDto mypageResponseDto = MypageResponseDto.builder().member(userDetails.getMember()).build();

        log.info("[마이페이지 조회 성공]");

        return mypageResponseDto;
    }

    @Transactional(readOnly = true)
    public MypageDetailResponseDto getDetailMypage(UserDetailsImpl userDetails, int page) {

        log.info("[마이페이지 상세 조회 요청]");

        Long memberId = userDetails.getMember().getId();
        Pageable pageable = PageRequest.of(page, 6, Sort.by(Sort.Direction.DESC, "id"));
        Page<Recipe> recipePage = recipeRepository.findByMemberIdOrderByIdDesc(memberId, pageable);

        List<Recipe> recipes = recipePage.getContent();
        List<Long> recipeIds = recipes.stream().map(Recipe::getId).toList();

        // 리뷰 수 조회
        Map<Long,Integer> reviewCountMap =
                reviewRepository.countByRecipeIds(recipeIds).stream()
                        .collect(Collectors.toMap(
                                row -> (Long) row[0],
                                row -> ((Long) row[1]).intValue()));

        // 좋아요 한 레시피 조회
        Set<Long> likedIds = recipeLikeRepository
                .findByMemberIdAndRecipeIdIn(memberId, recipeIds).stream()
                .map(RecipeLike::getRecipeId)
                .collect(Collectors.toSet());

        List<SimpleRecipeResponseDto> result = recipes.stream()
                .map(r -> SimpleRecipeResponseDto.builder()
                        .recipe(r)
                        .isLiked(likedIds.contains(r.getId()))
                        .reviewCount(reviewCountMap.getOrDefault(r.getId(), 0))
                        .build())
                .toList();

        Boolean isLikedMember = memberLikeRepository.existsByMemberIdAndLikedMemberId(memberId, memberId);

        log.info("[마이페이지 상세 조회 성공]");

        return MypageDetailResponseDto.builder()
                .member(userDetails.getMember()).isLiked(isLikedMember).simpleRecipeList(result).build();
    }

    @Transactional
    public MypageResponseDto updateMypage(UserDetailsImpl userDetails, MypageRequestDto mypageRequestDto, MultipartFile image) {

        log.info("[마이페이지 수정 요청]");

        Member member = memberRepository.findById(userDetails.getMember().getId())
                .orElseThrow(() -> {
                    log.warn("[유저 정보 조회 실패] 사용자 없음");
                    return new CustomException(ErrorCode.MEMBER_NOT_FOUND);
                });

        String imageUrl = member.getImageUrl();
        String newImageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                // 기존 이미지가 있으면 삭제
                if (imageUrl != null && !imageUrl.isBlank()) {
                    s3Service.delete(imageUrl);
                }
                newImageUrl = s3Service.upload(image, "profileImages");
            } catch (IOException e) {
                throw new CustomException(ErrorCode.FILE_CONVERT_FAIL);
            }
        }

        member.updateMember(mypageRequestDto, newImageUrl);

        log.info("[마이페이지 수정 성공]");

        return MypageResponseDto.builder().member(member).build();
    }

    @Transactional(readOnly = true)
    public List<SimplePurchaseResponseDto> getPurchaseHistory(UserDetailsImpl userDetails, int page) {

        log.info("[마이페이지 주문 내역 조회 요청]");

        Long memberId = userDetails.getMember().getId();

        Pageable pageable = PageRequest.of(page, 4, Sort.by(Sort.Direction.DESC, "purchaseDate"));
        Page<Purchase> purchasePage = purchaseRepository.findByMemberId(memberId, pageable);
        List<Purchase> purchases = purchasePage.getContent();

        if (purchases.isEmpty()) return List.of();

        Map<Long, Purchase> purchaseMap = purchases.stream()
                .collect(Collectors.toMap(Purchase::getId, Function.identity()));

        // 주문 id 리스트
        List<Long> purchaseIds = purchases.stream()
                .map(Purchase::getId).toList();

        // 주문-아이템 일괄 조회
        List<PurchaseItem> items = purchaseItemRepository.findByPurchaseIdIn(purchaseIds);

        // 상품 id 모으기
        List<Long> productIds = items.stream()
                .map(PurchaseItem::getProductId).distinct().toList();

        // 상품 - 한 번에 조회 → map
        Map<Long, Product> productMap = productRepository.findAllById(productIds)
                .stream().collect(Collectors.toMap(Product::getId, Function.identity()));

        // 판매자 id 모으기
        List<Long> sellerIds = productMap.values()
                .stream().map(Product::getMemberId).distinct().toList();

        // 판매자 - 한 번에 조회 → map(id → nickname)
        Map<Long, String> sellerNameMap = memberRepository.findAllById(sellerIds)
                .stream()
                .collect(Collectors.toMap(Member::getId, Member::getNickname));

        // DTO 변환
        List<SimplePurchaseResponseDto> simplePurchaseResponseDtos = items.stream()
                .map(item -> {
                    Purchase purchase = purchaseMap.get(item.getPurchaseId());
                    Product product = productMap.get(item.getProductId());
                    String sellerNickame = sellerNameMap.get(product.getMemberId());

                    return SimplePurchaseResponseDto.builder()
                            .purchaseItem(item)
                            .purchase(purchase)
                            .sellerNickname(sellerNickame)
                            .status(purchase.getStatus().getDescription())
                            .product(product)
                            .build();
                })
                .toList();

        log.info("[마이페이지 주문 내역 조회 성공]");

        return simplePurchaseResponseDtos;
    }

    @Transactional(readOnly = true)
    public List<SimpleProductResponseDto> getRecentViewedProducts(UserDetailsImpl userDetails, int page) {

        log.info("[마이페이지 최근 본 상품 조회 요청]");

        Long memberId = userDetails.getMember().getId();

        Pageable pageable = PageRequest.of(page, 6, Sort.by(Sort.Direction.DESC, "viewedAt"));
        Page<RecentProductView> views = recentProductViewRepository.findByMemberId(memberId, pageable);

        // 2. 조회된 뷰에서 상품 ID 추출
        List<Long> productIds = views.stream()
                .map(RecentProductView::getProductId)
                .distinct()
                .toList();

        // 3. 한 번에 상품 조회
        List<Product> products = productRepository.findAllById(productIds);

        // 4. productId -> Product 매핑
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        // 5. DTO 매핑 시 상품 정보 주입
        List<SimpleProductResponseDto> simpleProductResponseDtos = views.stream()
                .map(view -> {
                    Product product = productMap.get(view.getProductId());
                    Boolean isLikedProduct = productLikeRepository.existsByMemberIdAndProductId(memberId, product.getId());
                    return SimpleProductResponseDto.builder()
                            .product(product)
                            .isLiked(isLikedProduct)
                            .build();
                })
                .toList();

        log.info("[마이페이지 최근 본 상품 조회 성공]");

        return simpleProductResponseDtos;
    }

//    @Transactional(readOnly = true)
//    public MypageResponseDto getAllReviewHistory(UserDetailsImpl userDetails, int page) {
//
//        log.info("[마이페이지 전체 리뷰 내역 조회 요청]");
//
//        log.info("[마이페이지 전체 리뷰 내역 조회 성공]");
//    }
//
//    @Transactional(readOnly = true)
//    public MypageResponseDto getInquiryHistory(UserDetailsImpl userDetails, int page) {
//
//
//    }
//
//    @Transactional(readOnly = true)
//    public MypageResponseDto getProductReviewHistory(UserDetailsImpl userDetails, int page) {
//
//
//    }
//
//    @Transactional(readOnly = true)
//    public MypageResponseDto getRecipeReviewHistory(UserDetailsImpl userDetails, int page) {
//
//
//    }

//    @Transactional
//    public Void applyFarmer(UserDetailsImpl userDetails, ApplyFarmerRequestDto applyFarmerRequestDto,
//                            MultipartFile registFile, MultipartFile passbook, MultipartFile certifidoc, MultipartFile profile) {
//
//        log.info("[입점 신청 요청]");
//
//
//
//        log.info("[입점 신청 성공]");
//    }

    @Transactional(readOnly = true)
    public Boolean checkRegisNumDuplicated(String regisNum) {

        log.info("[사업자 등록번호 / 농가확인번호 중복 확인] regisNum={}", regisNum);

        if (memberRepository.existsByRegisNum(regisNum)) {
            log.warn("[중복 등록 번호] regisNum={}", regisNum);
            throw new CustomException(ErrorCode.REGIS_NUM_DUPLICATED);
        }
        return true;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveOrUpdate(Long memberId, Long productId) {
        recentProductViewRepository.saveOrUpdate(memberId, productId);
        log.info("[최근 본 상품 리스트 추가 성공]");
    }
}
