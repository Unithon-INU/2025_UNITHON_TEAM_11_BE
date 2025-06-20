package Uniton.Fring.domain.product.entity;

import jakarta.persistence.*;

@Entity
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String optionName;

    private Integer additionalPrice;

    @Column(name = "product_id", nullable = false)
    private Long productId;
}
