package Uniton.Fring.domain.product.dto.res;

import Uniton.Fring.domain.member.dto.res.MemberInfoResponseDto;
import Uniton.Fring.domain.product.entity.Product;
import Uniton.Fring.domain.review.ReviewResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Schema(description = "상품 상세 정보 응답 DTO")
public class ProductInfoResponseDto {

    @Schema(description = "농장", example = "고릴라 농장")
    private final MemberInfoResponseDto farm;

    @Schema(description = "상품명", example = "계란 30구, 1판")
    private final String name;

    @Schema(description = "상품 설명", example = "차돌박이를 이용한 구수한 된장찌개 레시피입니다.")
    private final ProductDescriptionResponseDto description;

    @Schema(description = "상품 후기", example = "차돌박이를 이용한 구수한 된장찌개 레시피입니다.")
    private final List<ReviewResponseDto> reviews;

    @Schema(description = "상품 대표 이미지 URL", example = "https://fring-s3.s3.ap-northeast-2.amazonaws.com/products/image.jpg")
    private final String imageUrl;

    @Schema(description = "상품 평점", example = "4.7")
    private final Double rating;

    @Schema(description = "상품 가격", example = "8700")
    private final BigDecimal price;

    @Schema(description = "상품 할인 가격", example = "6090")
    private final BigDecimal salePrice;

    @Schema(description = "택배사", example = "프링택배")
    private final String deliveryCompany;

    @Schema(description = "배송비", example = "2000원")
    private final Long deliveryFee;

    @Schema(description = "배송일정", example = "1/23(월) 이내 출발 예정")
    private final String deliverySchedule;

    @Schema(description = "연관 농수산품 목록", example = "꿀당근, 꿀멜론, 꿀")
    private final List<SimpleProductResponseDto> relatedProducts;

    @Schema(description = "포장", example = "일단포장함")
    private final String packaging;

    @Schema(description = "판매단위", example = "1판")
    private final String unit;

    @Schema(description = "중량/용량", example = "100kg")
    private final String volume;

    @Schema(description = "소비기한", example = "냄새나기 전까지")
    private final String expirationDate;

    @Schema(description = "총 리뷰수", example = "1234")
    private int totalReviewCount;

    @Schema(description = "총 사진 수", example = "24")
    private int totalImageCount;

    @Schema(description = "리뷰에 달린 사진들 (5개)", example = "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]")
    private List<String> recentImageUrls;

    // todo 문의?

    @Builder
    private ProductInfoResponseDto(Product product,
                                   MemberInfoResponseDto memberInfoResponseDto,
                                   ProductDescriptionResponseDto productDescriptionResponseDto,
                                   List<ReviewResponseDto> reviews,
                                   Integer totalReviewCount, int totalImageCount,
                                   List<String> recentImageUrls) {
        this.farm = memberInfoResponseDto;
        this.name = product.getName();
        this.description = productDescriptionResponseDto;
        this.reviews = reviews;
        this.imageUrl = product.getImageUrl();
        this.rating = product.getRating();
        this.price = product.getPrice();
        this.salePrice = product.getPrice().multiply(BigDecimal.valueOf(1 - product.getDiscountRate()));
        this.deliveryCompany = product.getDeliveryCompany();
        this.deliveryFee = product.getDeliveryFee();
        this.deliverySchedule = product.getDeliverySchedule();
        this.relatedProducts = new ArrayList<>();
        this.packaging = product.getPackaging();
        this.unit = product.getUnit();
        this.volume = product.getVolume();
        this.expirationDate = product.getExpirationDate();
        this.totalReviewCount = totalReviewCount;
        this.totalImageCount = totalImageCount;
        this.recentImageUrls = recentImageUrls;
    }
}
