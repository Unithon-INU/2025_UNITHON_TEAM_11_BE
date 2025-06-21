package Uniton.Fring.domain.like.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recipe_like")
public class RecipeLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "recipe_id", nullable = false)
    private Long recipeId;

    @Builder
    private RecipeLike(Long memberId, Long recipeId) {
        this.memberId = memberId;
        this.recipeId = recipeId;
    }
}
