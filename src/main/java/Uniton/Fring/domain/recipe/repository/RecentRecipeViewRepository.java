package Uniton.Fring.domain.recipe.repository;

import Uniton.Fring.domain.recipe.entity.RecentRecipeView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecentRecipeViewRepository extends JpaRepository<RecentRecipeView, Long> {

    Page<RecentRecipeView> findByMemberId(Long memberId, Pageable pageable);

    Optional<RecentRecipeView> findByMemberIdAndRecipeId(Long memberId, Long recipeId);

    default void saveOrUpdate(Long memberId, Long recipeId) {
        this.findByMemberIdAndRecipeId(memberId, recipeId).ifPresentOrElse(
                existing -> {
                    existing.updateViewTime();
                    save(existing);
                },
                () -> save(new RecentRecipeView(memberId, recipeId))
        );
    }
}
