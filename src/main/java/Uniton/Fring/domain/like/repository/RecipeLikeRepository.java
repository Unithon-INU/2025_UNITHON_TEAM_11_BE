package Uniton.Fring.domain.like.repository;

import Uniton.Fring.domain.like.entity.RecipeLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeLikeRepository extends JpaRepository<RecipeLike, Long> {

    boolean existsByMemberIdAndRecipeId(Long memberId, Long recipeId);

    void deleteByMemberIdAndRecipeId(Long memberId, Long recipeId);
}
