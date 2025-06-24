package Uniton.Fring.domain.cart;

import Uniton.Fring.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    private String productName;

    private String productOption;

    private int quantity;

    @Column(name = "product_price", nullable = false)
    private BigDecimal productPrice;

    @Column(name = "delivery_fee", nullable = false)
    private BigDecimal deliveryFee;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private Cart(Long memberId, Product product, String productOption, int quantity) {
        this.memberId = memberId;
        this.productId = product.getId();
        this.productName = product.getName();
        this.productOption = productOption;
        this.quantity = quantity;
        this.productPrice = product.getPrice().multiply(BigDecimal.valueOf(1 - product.getDiscountRate()));
        this.deliveryFee = product.getDeliveryFee();
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }
}