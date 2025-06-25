package Uniton.Fring.domain.purchase.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
    PENDING("상품 준비중"),
    SHIPPED("배송 중"),
    DELIVERED("배송 완료"),
    CANCELLED("상품 취소");

    private final String description;
}
