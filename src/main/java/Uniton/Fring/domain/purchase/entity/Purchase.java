package Uniton.Fring.domain.purchase.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "purchase")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "delivery_id", nullable = false)
    private Long deliveryId;

    @Column(nullable = false, unique = true, length = 16)
    private String purchaseNumber;

    @Column(nullable = false)
    private BigDecimal productPrice;

    @Column(nullable = false)
    private BigDecimal deliveryFee;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private String payMethod;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @Column(name = "purchase_date", nullable = false, updatable = false)
    private LocalDate purchaseDate;

    private String returnReason;

    private String returnDetailReason;

    private String returnImageUrls;

    private String returnFee;

    @Builder
    private Purchase(Long memberId, Long deliveryId, String purchaseNumber, BigDecimal productPrice,
                     BigDecimal deliveryFee, BigDecimal totalPrice, String payMethod) {
        this.memberId = memberId;
        this.deliveryId = deliveryId;
        this.purchaseNumber = purchaseNumber;
        this.productPrice = productPrice;
        this.deliveryFee = deliveryFee;
        this.totalPrice = totalPrice;
        this.payMethod = payMethod;
    }

    @PrePersist
    protected void onCreate() {
        this.purchaseDate = LocalDate.now();
    }
}