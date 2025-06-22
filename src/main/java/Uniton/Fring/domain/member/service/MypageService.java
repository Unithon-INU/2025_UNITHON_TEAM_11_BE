package Uniton.Fring.domain.member.service;

import Uniton.Fring.domain.like.repository.ProductLikeRepository;
import Uniton.Fring.domain.member.dto.req.MypageRequestDto;
import Uniton.Fring.domain.member.dto.res.MypageResponseDto;
import Uniton.Fring.domain.member.entity.Member;
import Uniton.Fring.domain.member.repository.MemberRepository;
import Uniton.Fring.domain.order.Order;
import Uniton.Fring.domain.order.OrderRepository;
import Uniton.Fring.domain.order.SimpleOrderResponseDto;
import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.domain.product.entity.Product;
import Uniton.Fring.domain.product.entity.RecentProductView;
import Uniton.Fring.domain.product.repository.ProductRepository;
import Uniton.Fring.domain.product.repository.RecentProductViewRepository;
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
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MypageService {

    private final S3Service s3Service;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final RecipeRepository recipeRepository;
    private final ReviewRepository reviewRepository;
    private final RecentProductViewRepository recentProductViewRepository;
    private final ProductLikeRepository productLikeRepository;

    @Transactional(readOnly = true)
    public MypageResponseDto getMypage(UserDetailsImpl userDetails) {

        log.info("[마이페이지 조회 요청]");

        MypageResponseDto mypageResponseDto = MypageResponseDto.builder().member(userDetails.getMember()).build();

        log.info("[마이페이지 조회 성공]");

        return mypageResponseDto;
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
    public List<SimpleOrderResponseDto> getOrderHistory(UserDetailsImpl userDetails, int page) {

        log.info("[마이페이지 주문 내역 조회 요청]");

        Long memberId = userDetails.getMember().getId();

        Pageable pageable = PageRequest.of(page, 4, Sort.by(Sort.Direction.DESC, "orderedAt"));
        Page<Order> orders = orderRepository.findByMemberId(memberId, pageable);

        // 주문에서 productId 리스트 추출
        List<Long> productIds = orders.stream()
                .map(Order::getProductId)
                .distinct()
                .toList();

        // 한 번에 상품 조회
        List<Product> products = productRepository.findAllById(productIds);

        // productId -> Product 매핑
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        List<SimpleOrderResponseDto> simpleOrderResponseDtos = orders.stream()
                        .map(order -> {
                            Product product = productMap.get(order.getProductId());

                            return SimpleOrderResponseDto.builder()
                                    .order(order).status(order.getStatus().getDescription()).product(product).build();
                        })
                        .toList();

        log.info("[마이페이지 주문 내역 조회 성공]");

        return simpleOrderResponseDtos;
    }

    @Transactional(readOnly = true)
    public List<SimpleProductResponseDto> getRecentViewedProducts(UserDetailsImpl userDetails, int page) {

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
//
//    @Transactional
//    public Void applyFarmer(UserDetailsImpl userDetails, ApplyFarmerRequestDto applyFarmerRequestDto) {
//
//
//    }
//
//    @Transactional(readOnly = true)
//    public Boolean checkRegisNumDuplicated(String regisNum) {
//
//
//    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveOrUpdate(Long memberId, Long productId) {
        recentProductViewRepository.saveOrUpdate(memberId, productId);
        log.info("[최근 본 상품 리스트 추가 성공]");
    }
}
