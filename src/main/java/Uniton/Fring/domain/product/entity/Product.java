package Uniton.Fring.domain.product.entity;

import Uniton.Fring.domain.product.dto.req.AddProductRequestDto;
import Uniton.Fring.domain.product.dto.req.UpdateProductRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    private String mainImageUrl;

    @Column(nullable = false)
    private String description;

    @ElementCollection
    @CollectionTable(name = "product_description_image", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> descriptionImageUrl;

    @Column(nullable = false)
    private BigDecimal price;

    // 10% 할인일 경우 값은 0.1
    @Column(name = "discount_rate", nullable = false)
    private Double discountRate = 0.0;

    @Column(nullable = false)
    private Double rating;

    @Column(nullable = false)
    private int totalStock;

    @Column(nullable = false)
    private String deliveryCompany;

    @Column(nullable = false)
    private Long deliveryFee;

    @Column(nullable = false)
    private String deliverySchedule;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String packaging;

    @Column(nullable = false)
    private String volume;

    @Column(nullable = false)
    private String harvestPeriod;

    @Column(nullable = false)
    private String expirationDate;

    private String additionalInfo;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Product(Long memberId, AddProductRequestDto addProductRequestDto, String mainImageUrl, List<String> descriptionImages) {
        this.memberId = memberId;
        this.name = addProductRequestDto.getName();
        this.mainImageUrl = mainImageUrl;
        this.description = addProductRequestDto.getDescription();
        this.descriptionImageUrl = descriptionImages;
        this.price = addProductRequestDto.getPrice();
        this.discountRate = addProductRequestDto.getDiscountRatePercent() / 100.0;
        this.rating = 0.0;
        this.totalStock = addProductRequestDto.getTotalStock();
        this.deliveryCompany = addProductRequestDto.getDeliveryCompany();
        this.deliveryFee = addProductRequestDto.getDeliveryFee();
        this.deliverySchedule = addProductRequestDto.getDeliverySchedule();
        this.origin = addProductRequestDto.getOrigin();
        this.packaging = addProductRequestDto.getPackaging();
        this.volume = addProductRequestDto.getVolume();
        this.harvestPeriod = addProductRequestDto.getHarvestPeriod();
        this.expirationDate = addProductRequestDto.getExpirationDate();
        this.additionalInfo = addProductRequestDto.getAdditionalInfo();
        this.createdAt = LocalDateTime.now();
    }

    public void updateProduct(UpdateProductRequestDto updateProductRequestDto, String mainImageUrl, List<String> descriptionImages) {
        this.name = updateProductRequestDto.getName();
        this.mainImageUrl = mainImageUrl;
        this.description = updateProductRequestDto.getDescription();
        this.descriptionImageUrl = descriptionImages;
        this.price = updateProductRequestDto.getPrice();
        this.discountRate = updateProductRequestDto.getDiscountRatePercent() / 100.0;
        this.totalStock = updateProductRequestDto.getTotalStock();
        this.deliveryCompany = updateProductRequestDto.getDeliveryCompany();
        this.deliveryFee = updateProductRequestDto.getDeliveryFee();
        this.deliverySchedule = updateProductRequestDto.getDeliverySchedule();
        this.origin = updateProductRequestDto.getOrigin();
        this.packaging = updateProductRequestDto.getPackaging();
        this.volume = updateProductRequestDto.getVolume();
        this.harvestPeriod = updateProductRequestDto.getHarvestPeriod();
        this.expirationDate = updateProductRequestDto.getExpirationDate();
        this.additionalInfo = updateProductRequestDto.getAdditionalInfo();
    }
}