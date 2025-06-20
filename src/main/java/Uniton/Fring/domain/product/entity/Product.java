package Uniton.Fring.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false)
    private String description;

    @Column(name = "description_image_url")
    private String descriptionImageUrl;

    @Column(nullable = false)
    private BigDecimal price;

    // 10% 할인일 경우 값은 0.1
    @Column(name = "discount_rate", nullable = false)
    private Double discountRate = 0.0;

    @Column(nullable = false)
    private Double rating;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private String deliveryCompany;

    @Column(nullable = false)
    private Long deliveryFee;

    @Column(nullable = false)
    private String deliverySchedule;

    @Column(nullable = false)
    private String packaging;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false)
    private String volume;

    @Column(nullable = false)
    private String expirationDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}