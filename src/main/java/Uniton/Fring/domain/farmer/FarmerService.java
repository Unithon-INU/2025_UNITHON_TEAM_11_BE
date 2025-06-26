package Uniton.Fring.domain.farmer;

import Uniton.Fring.domain.like.entity.ProductLike;
import Uniton.Fring.domain.like.repository.ProductLikeRepository;
import Uniton.Fring.domain.farmer.dto.res.StoreItemsResponseDto;
import Uniton.Fring.domain.member.entity.Member;
import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.domain.product.entity.Product;
import Uniton.Fring.domain.product.repository.ProductRepository;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FarmerService {

    private final ProductRepository productRepository;
    private final ProductLikeRepository productLikeRepository;

    @Transactional(readOnly = true)
    public StoreItemsResponseDto<SimpleProductResponseDto> getStore(UserDetailsImpl userDetails, int page) {

        Member member = userDetails.getMember();
        log.info("[스토어 관리 조회 요청] 회원: {}", member.getUsername());

        Pageable pageable = PageRequest.of(page, 6);
        Page<Product> productPage = productRepository.findByMemberIdOrderByCreatedAtDesc(member.getId(), pageable);

        List<Product> products = productPage.getContent();
        List<Long> productIds = products.stream().map(Product::getId).toList();

        Set<Long> likedIds = productLikeRepository
                .findByMemberIdAndProductIdIn(member.getId(), productIds)
                .stream().map(ProductLike::getProductId).collect(Collectors.toSet());

        List<SimpleProductResponseDto> result = productPage.getContent().stream()
                .map(product -> SimpleProductResponseDto.builder()
                        .product(product)
                        .isLiked(likedIds.contains(product.getId()))
                        .build())
                .toList();

        log.info("[스토어 관리 조회 성공] 회원: {}", member.getUsername());

        return StoreItemsResponseDto.<SimpleProductResponseDto>builder()
                .totalCount((int) productPage.getTotalElements())
                .items(result)
                .build();
    }
}
