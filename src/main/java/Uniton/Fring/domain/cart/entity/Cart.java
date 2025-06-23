package Uniton.Fring.domain.cart.entity;

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

    private String productOption;

    private int quantity;

    @Column(name = "product_price", nullable = false)
    private BigDecimal productPrice;

    @Column(name = "option_price", nullable = false)
    private BigDecimal optionPrice;

    @Column(name = "delivery_fee", nullable = false)
    private BigDecimal deliveryFee;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private Cart(Long memberId, Long productId,String productOption, int quantity, BigDecimal productPrice,
                 BigDecimal optionPrice, BigDecimal deliveryFee, BigDecimal totalPrice) {
        this.memberId = memberId;
        this.productId = productId;
        this.productOption = productOption;
        this.quantity = quantity;
        this.productPrice = productPrice;
        this.optionPrice = optionPrice;
        this.deliveryFee = deliveryFee;
        this.totalPrice = totalPrice;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}