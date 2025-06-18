package Uniton.Fring.domain.review;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    int countByRecipeId(Long recipeId);
}
