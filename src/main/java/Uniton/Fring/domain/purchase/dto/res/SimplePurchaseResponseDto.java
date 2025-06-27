package Uniton.Fring.domain.purchase.dto.res;

import Uniton.Fring.domain.product.entity.Product;
import Uniton.Fring.domain.purchase.entity.Purchase;
import Uniton.Fring.domain.purchase.entity.PurchaseItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Schema(description = "구매 미리보기 정보 응답 DTO")
public class SimplePurchaseResponseDto {

    @Schema(description = "주문 ID", example = "1")
    private final Long id;

    @Schema(description = "상품 ID", example = "1")
    private final Long productId;

    @Schema(description = "상품 준비 상태", example = "상품 준비 중")
    private final String status;

    @Schema(description = "판매자 닉네임", example = "병아리 농장")
    private final String sellerNickname;

    @Schema(description = "상품명", example = "토마토 행사")
    private final String productName;

    @Schema(description = "상품 옵션", example = "방울토마토")
    private final String productOption;

    @Schema(description = "수량", example = "1")
    private final Integer quantity;

    @Schema(description = "구매 가격", example = "9000")
    private final BigDecimal price;

    @Schema(description = "상품 이미지 URL", example = "https://your-bucket.s3.ap-northeast-2.amazonaws.com/products/apple.jpg")
    private final String imageUrl;

    @Schema(description = "주문 일자", example = "2025-01-01")
    private final LocalDate purchaseDate;

    @Schema(description = "리뷰 작성 여부", example = "true")
    private final Boolean isReviewed;

    @Builder
    private SimplePurchaseResponseDto(PurchaseItem purchaseItem, Purchase purchase, String sellerNickname, String status, Product product, Boolean isReviewed) {
        this.id = purchaseItem.getPurchaseId();
        this.productId = purchaseItem.getProductId();
        this.status = status;
        this.sellerNickname = sellerNickname;
        this.productName = product.getName();
        this.productOption = purchaseItem.getProductOption();
        this.quantity = purchaseItem.getQuantity();
        this.price = purchaseItem.getProductPrice();
        this.imageUrl = product.getMainImageUrl();
        this.purchaseDate = purchase.getPurchaseDate();
        this.isReviewed = isReviewed;
    }
}
