package Uniton.Fring.domain.review.entity;

import Uniton.Fring.domain.review.dto.req.ProductReviewRequestDto;
import Uniton.Fring.domain.review.dto.req.RecipeReviewRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "product_id", nullable = true)
    private Long productId;

    @Column(name = "recipe_id", nullable = true)
    private Long recipeId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer rating;

    @ElementCollection
    @CollectionTable(name = "review_image", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;

    @Column(name = "purchase_option", nullable = true)
    private String purchaseOption;

    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @Column(nullable = false)
    private Integer likeCount = 0;

    @Builder
    private Review(Long memberId, Long productId, Long recipeId, String content, Integer rating, List<String> imageUrls, String purchaseOption) {
        this.memberId = memberId;
        this.productId = productId;
        this.recipeId = recipeId;
        this.content = content;
        this.rating = rating;
        this.imageUrls = imageUrls;
        this.purchaseOption = purchaseOption;
    }

    public static Review fromProductReview(Long memberId, ProductReviewRequestDto dto, List<String> imageUrls, String purchaseOption) {
        return new Review(memberId, dto.getProductId(), null, dto.getContent(), dto.getRating(), imageUrls, purchaseOption);
    }

    public static Review fromRecipeReview(Long memberId, RecipeReviewRequestDto dto, List<String> imageUrls) {
        return new Review(memberId, null, dto.getRecipeId(), dto.getContent(), dto.getRating(), imageUrls, null);
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDate.now();
    }

    public void increaseLikeCount() {
        likeCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }
}