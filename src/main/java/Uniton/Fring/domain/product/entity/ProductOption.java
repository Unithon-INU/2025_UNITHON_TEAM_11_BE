package Uniton.Fring.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product_option")
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "option_name", nullable = false)
    private String optionName;

    @Column(name = "additional_price", nullable = false)
    private BigDecimal additionalPrice;

    @Column(nullable = false)
    private Boolean available;

    public ProductOption(Long productId, String optionName, BigDecimal additionalPrice) {
        this.productId = productId;
        this.optionName = optionName;
        this.additionalPrice = additionalPrice;
        this.available = true;
    }
}
