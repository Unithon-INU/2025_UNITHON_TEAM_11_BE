package Uniton.Fring.domain.member.service;

import Uniton.Fring.domain.farmer.Farmer;
import Uniton.Fring.domain.farmer.FarmerRepository;
import Uniton.Fring.domain.farmer.dto.res.StoreResponseDto;
import Uniton.Fring.domain.like.entity.RecipeLike;
import Uniton.Fring.domain.like.entity.ReviewLike;
import Uniton.Fring.domain.like.repository.MemberLikeRepository;
import Uniton.Fring.domain.like.repository.ProductLikeRepository;
import Uniton.Fring.domain.like.repository.RecipeLikeRepository;
import Uniton.Fring.domain.like.repository.ReviewLikeRepository;
import Uniton.Fring.domain.member.dto.req.ApplyFarmerRequestDto;
import Uniton.Fring.domain.member.dto.req.MypageRequestDto;
import Uniton.Fring.domain.member.dto.res.MypageDetailResponseDto;
import Uniton.Fring.domain.member.dto.res.MypageResponseDto;
import Uniton.Fring.domain.member.dto.res.MypageReviewResponseDto;
import Uniton.Fring.domain.member.entity.Member;
import Uniton.Fring.domain.member.enums.MemberRole;
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
import Uniton.Fring.domain.recipe.entity.RecentRecipeView;
import Uniton.Fring.domain.recipe.entity.Recipe;
import Uniton.Fring.domain.recipe.repository.RecentRecipeViewRepository;
import Uniton.Fring.domain.recipe.repository.RecipeRepository;
import Uniton.Fring.domain.review.dto.res.ReviewResponseDto;
import Uniton.Fring.domain.review.entity.Review;
import Uniton.Fring.domain.review.repository.ReviewRepository;
import Uniton.Fring.domain.review.service.ReviewService;
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
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MypageService {

    private final S3Service s3Service;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final ProductLikeRepository productLikeRepository;
    private final RecentProductViewRepository recentProductViewRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeLikeRepository recipeLikeRepository;
    private final RecentRecipeViewRepository recentRecipeViewRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewService reviewService;
    private final PurchaseRepository purchaseRepository;
    private final PurchaseItemRepository purchaseItemRepository;
    private final MemberLikeRepository memberLikeRepository;
    private final FarmerRepository farmerRepository;

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

        member.updateMember(mypageRequestDto.getNickname(), mypageRequestDto.getIntroduction(), newImageUrl);

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

        // 리뷰 조회: purchaseId 기준만 사용
        List<Review> reviews = reviewRepository.findByMemberIdAndPurchaseIdIn(memberId, purchaseIds);

        // 이미 리뷰가 있는 purchaseId 집합
        Set<Long> reviewedPurchaseIds = reviews.stream()
                .map(Review::getPurchaseId)
                .collect(Collectors.toSet());

        // DTO 변환
        List<SimplePurchaseResponseDto> simplePurchaseResponseDtos = items.stream()
                .map(item -> {
                    Purchase purchase = purchaseMap.get(item.getPurchaseId());
                    Product product = productMap.get(item.getProductId());
                    String sellerNickame = sellerNameMap.get(product.getMemberId());
                    boolean isReviewed = reviewedPurchaseIds.contains(item.getPurchaseId());

                    return SimplePurchaseResponseDto.builder()
                            .purchaseItem(item)
                            .purchase(purchase)
                            .sellerNickname(sellerNickame)
                            .status(purchase.getPurchaseStatus().getDescription())
                            .product(product)
                            .isReviewed(isReviewed)
                            .build();
                })
                .toList();

        log.info("[마이페이지 주문 내역 조회 성공]");

        return simplePurchaseResponseDtos;
    }

    @Transactional(readOnly = true)
    public List<SimpleProductResponseDto> getRecentViewedProducts(UserDetailsImpl userDetails, int page) {

        log.info("[최근 본 상품 조회 요청] 회원: {}", userDetails.getMember().getNickname());

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

        log.info("[최근 본 상품 조회 성공]");

        return simpleProductResponseDtos;
    }

    @Transactional(readOnly = true)
    public List<SimpleRecipeResponseDto> getRecentViewedRecipes(UserDetailsImpl userDetails, int page) {

        log.info("[최근 본 레시피 조회 요청], 회원: {}", userDetails.getMember().getNickname());

        Long memberId = userDetails.getMember().getId();

        Pageable pageable = PageRequest.of(page, 6, Sort.by(Sort.Direction.DESC, "viewedAt"));
        Page<RecentRecipeView> views = recentRecipeViewRepository.findByMemberId(memberId, pageable);

        // 조회된 뷰에서 레시피 ID 추출
        List<Long> recipeIds = views.stream()
                .map(RecentRecipeView::getRecipeId)
                .distinct()
                .toList();

        // 한 번에 레시피 조회
        List<Recipe> recipes = recipeRepository.findAllById(recipeIds);

        // recipeId -> Product 매핑
        Map<Long, Recipe> recipeMap = recipes.stream()
                .collect(Collectors.toMap(Recipe::getId, Function.identity()));

        Map<Long, Integer> reviewCountMap = reviewService.getReviewCountMapFromRecipes(recipes);

        // DTO 매핑 시 레시피 정보 주입
        List<SimpleRecipeResponseDto> simpleRecipeResponseDtos = views.stream()
                .map(view -> {
                    Recipe recipe = recipeMap.get(view.getRecipeId());
                    Boolean isLikedRecipe = recipeLikeRepository.existsByMemberIdAndRecipeId(memberId, recipe.getId());
                    Integer reviewCount = reviewCountMap.getOrDefault(recipe.getId(), 0);
                    return SimpleRecipeResponseDto.builder()
                            .recipe(recipe)
                            .isLiked(isLikedRecipe)
                            .reviewCount(reviewCount)
                            .build();
                })
                .toList();

        log.info("[최근 본 레시피 조회 성공]");

        return simpleRecipeResponseDtos;
    }

    @Transactional(readOnly = true)
    public MypageReviewResponseDto getMyReviews(UserDetailsImpl userDetails, int page) {

        Member member = userDetails.getMember();
        Long memberId = member.getId();
        log.info("[나의 리뷰 조회 요청] 회원: {}", member.getUsername());

        Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "createdAt"));

        // 상품 리뷰
        Page<Review> productReviewPage = reviewRepository.findByMemberIdAndProductIdIsNotNull(memberId, pageable);

        // 레시피 리뷰
        Page<Review> recipeReviewPage = reviewRepository.findByMemberIdAndRecipeIdIsNotNull(memberId, pageable);

        // 두 페이지 좋아요 여부
        List<Long> allReviewIds = Stream
                .concat(productReviewPage.getContent().stream(), recipeReviewPage.getContent().stream())
                .map(Review::getId)
                .toList();

        // 좋아요 수집
        Set<Long> likedReviewIds = reviewLikeRepository
                .findByMemberIdAndReviewIdIn(memberId, allReviewIds)
                .stream()
                .map(ReviewLike::getReviewId)
                .collect(Collectors.toSet());

        List<ReviewResponseDto> productReviews = productReviewPage.getContent().stream()
                .map(productReview -> ReviewResponseDto.builder()
                        .review(productReview)
                        .memberInfo(null)
                        .isLiked(likedReviewIds.contains(productReview.getId()))
                        .purchaseOption(productReview.getPurchaseOption())
                        .build())
                .toList();


        List<ReviewResponseDto> recipeReviews = recipeReviewPage.getContent().stream()
                .map(recipeReview -> ReviewResponseDto.builder()
                        .review(recipeReview)
                        .memberInfo(null)
                        .isLiked(likedReviewIds.contains(recipeReview.getId()))
                        .purchaseOption(null)
                        .build())
                .toList();

        log.info("[나의 리뷰 조회 성공]");

        return MypageReviewResponseDto.builder().productReviews(productReviews).recipeReviews(recipeReviews).build();
    }

    @Transactional
    public StoreResponseDto applyFarmer(UserDetailsImpl userDetails, ApplyFarmerRequestDto applyFarmerRequestDto,
                                        MultipartFile registFile, MultipartFile passbook, MultipartFile certifidoc, MultipartFile profile) {

        log.info("[입점 신청 요청]");

        if (userDetails.getMember().getRole() == MemberRole.FARMER) {
            log.warn("이미 농부인 회원입니다. 회원: {}", userDetails.getMember().getUsername());
            throw new CustomException(ErrorCode.ALREADY_FARMER);
        }

        String profileImageUrl = s3Service.uploadProfileImage(profile, "profileImages");
        String registFileUrl = s3Service.uploadFile(registFile, "registFiles");
        String passbookUrl = s3Service.uploadFile(passbook, "passbookImages");
        String certifidocUrl = s3Service.uploadFile(certifidoc, "certifidocs");

        Member member = memberRepository.findById(userDetails.getMember().getId())
                .orElseThrow(() -> {
                    log.warn("[로그인 실패] 사용자 없음: username={}", userDetails.getMember().getUsername());
                    return new CustomException(ErrorCode.MEMBER_NOT_FOUND);
                });

        Farmer farmer = farmerRepository.findByMemberId(member.getId())
                .orElseGet(() -> Farmer.builder().memberId(member.getId()).build());

        member.updateMember(applyFarmerRequestDto.getMarketName(), applyFarmerRequestDto.getIntro(), profileImageUrl);
        farmer.applyFarmer(applyFarmerRequestDto, registFileUrl, passbookUrl, certifidocUrl);
        farmerRepository.save(farmer);

        log.info("[입점 신청 성공]");

        return StoreResponseDto.builder().member(member).farmer(farmer).build();
    }

    @Transactional(readOnly = true)
    public Boolean checkRegisNumDuplicated(String regisNum) {

        log.info("[사업자 등록번호 / 농가확인번호 중복 확인] regisNum={}", regisNum);

        if (farmerRepository.existsByRegisNum(regisNum)) {
            log.warn("[중복 등록 번호] regisNum={}", regisNum);
            throw new CustomException(ErrorCode.REGIS_NUM_DUPLICATED);
        }
        return true;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recentProductSaveOrUpdate(Long memberId, Long productId) {
        recentProductViewRepository.saveOrUpdate(memberId, productId);
        log.info("[최근 본 상품 리스트 업데이트 성공]");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recentRecipeSaveOrUpdate(Long memberId, Long recipeId) {
        recentRecipeViewRepository.saveOrUpdate(memberId, recipeId);
        log.info("[최근 본 레시피 리스트 업데이트 성공]");
    }
}
