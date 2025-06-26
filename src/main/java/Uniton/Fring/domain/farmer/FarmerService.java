package Uniton.Fring.domain.farmer;

import Uniton.Fring.domain.farmer.dto.req.UpdateStoreRequestDto;
import Uniton.Fring.domain.farmer.dto.res.StoreItemsResponseDto;
import Uniton.Fring.domain.farmer.dto.res.StoreResponseDto;
import Uniton.Fring.domain.like.entity.ProductLike;
import Uniton.Fring.domain.like.repository.ProductLikeRepository;
import Uniton.Fring.domain.member.entity.Member;
import Uniton.Fring.domain.member.repository.MemberRepository;
import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.domain.product.entity.Product;
import Uniton.Fring.domain.product.repository.ProductRepository;
import Uniton.Fring.global.exception.CustomException;
import Uniton.Fring.global.exception.ErrorCode;
import Uniton.Fring.global.s3.S3Service;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FarmerService {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final ProductLikeRepository productLikeRepository;
    private final S3Service s3Service;
    private final FarmerRepository farmerRepository;

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

    @Transactional
    public StoreResponseDto updateStore(UserDetailsImpl userDetails,
                                        UpdateStoreRequestDto updateStoreRequestDto, MultipartFile image) {

        log.info("[스토어 정보 수정 요청]");

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

        Farmer farmer = farmerRepository.findByMemberId(member.getId())
                .orElseGet(() -> Farmer.builder().memberId(member.getId()).build());

        farmer.updateStore(updateStoreRequestDto);
        farmerRepository.save(farmer);

        member.updateMember(updateStoreRequestDto.getNickname(), updateStoreRequestDto.getIntroduction(), newImageUrl);

        log.info("[스토어 정보 수정 성공]");

        return StoreResponseDto.builder().member(member).build();
    }
}
