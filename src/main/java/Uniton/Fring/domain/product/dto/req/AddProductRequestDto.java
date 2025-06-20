package Uniton.Fring.domain.product.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@Schema(description = "상품 정보 추가 요청 DTO ")
public class AddProductRequestDto {

    @NotBlank(message = "대표 이미지가 비어있습니다.")
    @Schema(description = "상품 대표 이미지 URL", example = "https://fring-s3.s3.ap-northeast-2.amazonaws.com/products/Broccoli.png")
    private String imageUrl;

    @NotBlank
    @Schema(description = "상품 이름", example = "유기농 브로콜리, 500g, 1봉")
    private String name;

    @NotBlank
    @Schema(description = "요약 설명", example = "신선하고 건강한 유기농 브로콜리입니다.")
    private String description;

    @Schema(description = "상세 이미지", example = "https://fring-s3.s3.ap-northeast-2.amazonaws.com/products/Broccoli_desc.png")
    private String descriptionImageUrl;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Schema(description = "상품 가격", example = "8700")
    private BigDecimal price;

    @NotNull
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "1.0")
    @Schema(description = "할인율", example = "0.1") // 10% 할인은 0.1
    private Double discountRate;

    @NotNull
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5.0")
    @Schema(description = "상품 평점", example = "4.5")
    private Double rating;

    // 상품 옵션

    @NotBlank
    @Schema(description = "택배사", example = "프링택배")
    private String deliveryCompany;

    @NotNull
    @Min(0)
    @Schema(description = "배송비", example = "2000")
    private Long deliveryFee;

    @NotBlank
    @Schema(description = "배송 일정", example = "1/23(월) 이내 출발 예정")
    private String deliverySchedule;

    @NotBlank
    @Schema(description = "원산지", example = "제주도")
    private String origin;

    @NotBlank
    @Schema(description = "재배 방식", example = "무농약 재배")
    private String farmingMethod;

    @NotBlank
    @Schema(description = "수확 시기", example = "2025년 6월 초 수확")
    private String harvestPeriod;

    @NotBlank
    @Schema(description = "포장 정보", example = "일단포장함")
    private String packaging;

    @NotBlank
    @Schema(description = "판매 단위", example = "1판")
    private String unit;

    @NotBlank
    @Schema(description = "중량/용량", example = "100kg")
    private String volume;

    @NotBlank
    @Schema(description = "소비기한", example = "냄새나기 전까지")
    private String expirationDate;
}
