package Uniton.Fring.domain.recipe.entity;

import Uniton.Fring.domain.recipe.dto.req.RecipeStepRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecipeStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int stepOrder;

    private String description;

    private String imageUrl;

    @Column(name = "recipe_id", nullable = false)
    private Long recipeId;

    @Builder
    private RecipeStep(RecipeStepRequestDto recipeStepRequestDto, Long recipeId) {
        this.stepOrder = recipeStepRequestDto.getStepOrder();
        this.description = recipeStepRequestDto.getDescription();
        this.imageUrl = recipeStepRequestDto.getImageUrl();
        this.recipeId = recipeId;
    }
}
