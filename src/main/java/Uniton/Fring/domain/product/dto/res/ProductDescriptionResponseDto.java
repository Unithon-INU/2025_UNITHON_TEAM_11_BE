package Uniton.Fring.domain.product.dto.res;

import Uniton.Fring.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "상품 설명 DTO")
public class ProductDescriptionResponseDto {

    @Schema(description = "요약 설명", example = "맛 좋은 토마토.")
    private String description;

    @Schema(description = "상품 설명 이미지 URL", example = "https://fring-s3.s3.ap-northeast-2.amazonaws.com/products/image.jpg")
    private String descriptionImageUrl;

    @Builder
    private ProductDescriptionResponseDto(Product product) {
        this.description = product.getDescription();
        this.descriptionImageUrl = product.getDescriptionImageUrl();
    }
}