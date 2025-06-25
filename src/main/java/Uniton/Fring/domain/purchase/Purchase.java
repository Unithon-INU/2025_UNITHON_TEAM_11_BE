package Uniton.Fring.domain.purchase;

import jakarta.persistence.*;
import lombok.AccessLevel;
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

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String purchaseNumber;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private String purchaseOption;

    @Column(nullable = false)
    private String payMethod;

    @Column(nullable = false)
    private String memberNickname;

    @Column(nullable = false)
    private String memberAddress;

    @Column(nullable = false)
    private  String memberPhoneNumber;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @Column(name = "purchase_date", nullable = false, updatable = false)
    private LocalDate purchaseDate;

    private String returnReason;

    private String returnDetailReason;

    private String returnImageUrls;

    private String returnFee;

    @PrePersist
    protected void onCreate() {
        this.purchaseDate = LocalDate.now();
    }
}