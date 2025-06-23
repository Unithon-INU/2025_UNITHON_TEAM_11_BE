package Uniton.Fring.domain.like.repository;

import Uniton.Fring.domain.like.entity.RecipeLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeLikeRepository extends JpaRepository<RecipeLike, Long> {

    boolean existsByMemberIdAndRecipeId(Long memberId, Long recipeId);

    void deleteByMemberIdAndRecipeId(Long memberId, Long recipeId);

    List<RecipeLike> findByMemberIdAndRecipeIdIn(Long memberId, List<Long> recipeIds);

    Page<RecipeLike> findByMemberId(Long memberId, Pageable pageable);

    long countByMemberId(Long memberId);
}
