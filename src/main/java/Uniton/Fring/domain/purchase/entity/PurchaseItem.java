package Uniton.Fring.domain.purchase.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "purchase_item")
public class PurchaseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "purchase_id", nullable = false)
    private Long purchaseId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = true)
    private String productOption;

    @Column(nullable = false)
    private BigDecimal productPrice;

    @Builder
    public PurchaseItem(Long purchaseId, Long productId, Integer quantity, String productOption, BigDecimal productPrice) {
        this.purchaseId = purchaseId;
        this.productId = productId;
        this.quantity = quantity;
        this.productOption = productOption;
        this.productPrice = productPrice;
    }
}
