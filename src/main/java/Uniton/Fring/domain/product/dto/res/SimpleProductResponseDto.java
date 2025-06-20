package Uniton.Fring.domain.product.dto.res;

import Uniton.Fring.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Schema(description = "상위 평점 상품 응답 DTO")
public class SimpleProductResponseDto {

    @Schema(description = "상품 ID", example = "1")
    private final Long id;

    @Schema(description = "상품명", example = "신선한 사과")
    private final String name;

    @Schema(description = "정가", example = "10000")
    private final BigDecimal price;

    @Schema(description = "판매가", example = "8000")
    private final BigDecimal salePrice;

    @Schema(description = "상품 이미지 URL", example = "https://your-bucket.s3.ap-northeast-2.amazonaws.com/products/apple.jpg")
    private final String image;

    @Schema(description = "찜 여부", example = "true")
    private final Boolean isLiked;

    @Builder
    public SimpleProductResponseDto(Product product, Boolean isLiked) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.salePrice = product.getPrice().multiply(BigDecimal.valueOf(1 - product.getDiscountRate()));
        this.image = product.getMainImageUrl();
        this.isLiked = isLiked;
    }
}
