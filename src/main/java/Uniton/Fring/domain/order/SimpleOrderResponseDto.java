package Uniton.Fring.domain.order;

import Uniton.Fring.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Schema(description = "주문 미리보기 정보 응답 DTO")
public class SimpleOrderResponseDto {

    @Schema(description = "주문 ID", example = "1")
    private final Long id;

    @Schema(description = "상품 준비 상태", example = "상품 준비 중")
    private final String status;

    @Schema(description = "상품명", example = "신선한 사과")
    private final String name;

    @Schema(description = "구매 가격", example = "9000")
    private final BigDecimal price;

    @Schema(description = "상품 이미지 URL", example = "https://your-bucket.s3.ap-northeast-2.amazonaws.com/products/apple.jpg")
    private final String imageUrl;

    @Schema(description = "주문 일자", example = "2025-01-01")
    private final LocalDate orderDate;

    @Builder
    private SimpleOrderResponseDto(Order order, String status, Product product) {
        this.id = order.getId();
        this.status = status;
        this.name = product.getName();
        this.price = order.getTotalPrice();
        this.imageUrl = product.getMainImageUrl();
        this.orderDate = order.getOrderedAt();
    }
}
