package Uniton.Fring.domain.purchase.dto.res;

import Uniton.Fring.domain.member.entity.Member;
import Uniton.Fring.domain.product.entity.Product;
import Uniton.Fring.domain.purchase.Purchase;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Schema(description = "구매 상세 정보 응답 DTO")
public class PurchaseResponseDto {

    @Schema(description = "주문 ID", example = "1")
    private final Long id;

    @Schema(description = "상품 ID", example = "1")
    private final Long productId;

    @Schema(description = "주문 일자", example = "2025-01-01")
    private final LocalDate purchaseDate;

    @Schema(description = "주문 번호", example = "123456789")
    private final String purchaseNumber;

    @Schema(description = "상품 준비 상태", example = "상품 준비 중")
    private final String status;

    @Schema(description = "상품 이미지 URL", example = "https://your-bucket.s3.ap-northeast-2.amazonaws.com/products/apple.jpg")
    private final String imageUrl;

    @Schema(description = "상품명", example = "토마토 행사")
    private final String productName;

    @Schema(description = "판매자 닉네임", example = "병아리 농장")
    private final String sellerNickname;

    @Schema(description = "상품 옵션", example = "방울 토마토")
    private final String purchaseOption;

    @Schema(description = "결제 방법", example = "프링 페이")
    private final String payMethod;

//    @Schema(description = "총 상품 금액", example = "6000")
//    private final BigDecimal productPrice;
//
//    @Schema(description = "배송비", example = "3000")
//    private final BigDecimal deliveryFee;
//
//    @Schema(description = "총 가격", example = "9000")
//    private final BigDecimal totalPrice;

    @Schema(description = "구매자 닉네임", example = "김프링")
    private final String consumerNickname;

//    @Schema(description = "배송지", example = "경기도 프링시 프링구 프링동 프링마 123-45, 607호")
//    private final String consumerAddress;
//
//    @Schema(description = "구매자 전화번호", example = "010-1234-5678")
//    private final String consumerPhoneNumber;

    @Builder
    private PurchaseResponseDto(Purchase purchase, String status, Product product, Member seller, Member consumer) {
        this.id = purchase.getId();
        this.productId = product.getId();
        this.purchaseDate = purchase.getPurchaseDate();
        this.purchaseNumber = purchase.getPurchaseNumber();
        this.status = status;
        this.imageUrl = product.getMainImageUrl();
        this.productName = product.getName();
        this.sellerNickname = seller.getNickname();
        this.purchaseOption = purchase.getPurchaseOption();
        this.payMethod = purchase.getPayMethod();
        this.consumerNickname = consumer.getNickname();
    }
}
