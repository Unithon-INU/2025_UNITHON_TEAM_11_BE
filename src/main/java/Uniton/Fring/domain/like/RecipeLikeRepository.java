package Uniton.Fring.domain.like;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeLikeRepository extends JpaRepository<RecipeLike, Long> {

    boolean existsByMemberIdAndRecipeId(Long memberId, Long recipeId);
}
