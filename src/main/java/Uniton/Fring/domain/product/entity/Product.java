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

    @Column(name = "main_image_url")
    private String mainImageUrl;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    @CollectionTable(name = "product_description_image", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "description_image_url")
    private List<String> descriptionImageUrl;

    @Column(nullable = false)
    private BigDecimal price;

    // 10% 할인일 경우 값은 0.1
    @Column(name = "discount_rate", nullable = false)
    private Double discountRate = 0.0;

    @Column(nullable = false)
    private Double rating;

    @Column(nullable = false)
    private String totalStock;

    @Column(nullable = false)
    private String deliveryCompany;

    @Column(nullable = false)
    private BigDecimal deliveryFee;

    @Column(nullable = false)
    private Integer deliverySchedule;

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

    @Column(nullable = false)
    private Integer likeCount = 0;

    public Product(Long memberId, AddProductRequestDto addProductRequestDto, String mainImageUrl) {
        this.memberId = memberId;
        this.name = addProductRequestDto.getName();
        this.mainImageUrl = mainImageUrl;
        this.description = addProductRequestDto.getDescription();
        this.price = addProductRequestDto.getPrice();
        double percent = addProductRequestDto.getDiscountRatePercent() != null
                ? addProductRequestDto.getDiscountRatePercent()
                : 0.0;
        this.discountRate = percent / 100.0;
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
        this.likeCount = 0;
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

    public void increaseLikeCount() {
        likeCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }

    public void updateRating(Double newRating) {
        this.rating = newRating != null ? newRating : 0.0;
    }
}