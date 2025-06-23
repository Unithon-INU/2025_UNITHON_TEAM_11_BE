package Uniton.Fring.domain.recipe.entity;

import Uniton.Fring.domain.recipe.dto.req.RecipeRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recipe")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "image_url")
    private String imageUrl;

    private Integer likeCount = 0;

    private Double rating;

    @Column(name = "head_count", nullable = false)
    private int headCount;

    @Column(name = "cooking_time", nullable = false)
    private String cookingTime;

    @Column(nullable = false)
    private String difficulty;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @ElementCollection
    @CollectionTable(name = "recipe_ingredient", joinColumns = @JoinColumn(name = "recipe_id"))
    @MapKeyColumn(name = "ingredient_name")
    @Column(name = "ingredient_amount")
    private Map<String, String> ingredients = new LinkedHashMap<>();

    @ElementCollection
    @CollectionTable(name = "recipe_sauce_ingredient", joinColumns = @JoinColumn(name = "recipe_id"))
    @MapKeyColumn(name = "sauce_ingredient_name")
    @Column(name = "sauce_ingredient_amount")
    private Map<String, String> sauces = new LinkedHashMap<>();

    @Builder
    private Recipe(Long memberId, RecipeRequestDto recipeRequestDto, String mainImageUrl) {
        this.memberId = memberId;
        this.title = recipeRequestDto.getTitle();
        this.content = recipeRequestDto.getContent();
        this.imageUrl = mainImageUrl;
        this.likeCount = 0;
        this.rating = recipeRequestDto.getRating();
        this.headCount = recipeRequestDto.getHeadCount();
        this.cookingTime = recipeRequestDto.getCookingTime();
        this.difficulty = recipeRequestDto.getDifficulty();
        this.createdAt = LocalDateTime.now();
        this.ingredients = recipeRequestDto.getIngredients();
        this.sauces = recipeRequestDto.getSauces();
    }

    public void updateRecipe(RecipeRequestDto recipeRequestDto, String mainImageUrl) {
        this.title = recipeRequestDto.getTitle();
        this.content = recipeRequestDto.getContent();
        this.imageUrl = mainImageUrl;
        this.rating = recipeRequestDto.getRating();
        this.headCount = recipeRequestDto.getHeadCount();
        this.cookingTime = recipeRequestDto.getCookingTime();
        this.difficulty = recipeRequestDto.getDifficulty();
        this.createdAt = LocalDateTime.now();
        this.ingredients = recipeRequestDto.getIngredients();
        this.sauces = recipeRequestDto.getSauces();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
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