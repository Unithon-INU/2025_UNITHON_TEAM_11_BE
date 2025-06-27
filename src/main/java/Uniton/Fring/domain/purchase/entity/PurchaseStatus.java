package Uniton.Fring.domain.purchase.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PurchaseStatus {
    PENDING("상품 준비중"),
    SHIPPED("배송 중"),
    DELIVERED("배송 완료"),
    CANCELED("상품 취소");

    private final String description;
}
