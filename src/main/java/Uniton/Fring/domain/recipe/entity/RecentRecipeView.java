package Uniton.Fring.domain.recipe.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recent_recipe_view")
public class RecentRecipeView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "recipe_id", nullable = false)
    private Long recipeId;

    @Column(name = "viewed_at", nullable = false)
    private LocalDateTime viewedAt;

    public RecentRecipeView(Long memberId, Long recipeId) {
        this.memberId = memberId;
        this.recipeId = recipeId;
        this.viewedAt = LocalDateTime.now();
    }

    public void updateViewTime() {
        this.viewedAt = LocalDateTime.now();
    }
}
