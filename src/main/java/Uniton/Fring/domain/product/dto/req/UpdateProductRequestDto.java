package Uniton.Fring.domain.product.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "상품 정보 수정 요청 DTO ")
public class UpdateProductRequestDto {

    @Schema(description = "대표 이미지", type = "string", format = "binary")
    private MultipartFile mainImage;

    @Schema(description = "상세 설명 이미지 목록", type = "array", format = "binary")
    private List<MultipartFile> descriptionImages;

    @NotBlank
    @Schema(description = "상품 이름", example = "계란 30구, 1판")
    private String name;

    @NotBlank
    @Schema(description = "요약 설명", example = "맛돌이 토종 달걀입니다. 아주 맛있어요.")
    private String description;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Schema(description = "상품 가격", example = "8700")
    private BigDecimal price;

    @Schema(description = "총 수량", example = "1판, 30알")
    private String totalStock;

    @NotBlank
    @Schema(description = "중량/용량", example = "30kg")
    private String volume;

    @NotNull
    @Min(0)
    @Max(100)
    @Schema(description = "할인율 (%)", example = "30")
    private Integer discountRatePercent;

    @Schema(description = "상품 옵션")
    private List<ProductOptionRequestDto> options;

    @NotBlank
    @Schema(description = "택배사", example = "프링택배")
    private String deliveryCompany;

    @NotNull
    @Min(0)
    @Schema(description = "배송비", example = "2500")
    private Long deliveryFee;

    @NotBlank
    @Schema(description = "배송 일정", example = "3 -> 주문일 기준 3일내 발송을 의미")
    private Integer deliverySchedule;

    @NotBlank
    @Schema(description = "원산지", example = "경기도 프링시 프링구")
    private String origin;

    @NotBlank
    @Schema(description = "수확 시기", example = "2025-01-15")
    private String harvestPeriod;

    @NotBlank
    @Schema(description = "소비기한", example = "1개월")
    private String expirationDate;

    @NotBlank
    @Schema(description = "보관 방법", example = "알아서 잘 보관한다.")
    private String packaging;

    @Schema(description = "기타 설명", example = "무농약으로 재배되어 안심하고 드실 수 있습니다.")
    private String additionalInfo;
}
