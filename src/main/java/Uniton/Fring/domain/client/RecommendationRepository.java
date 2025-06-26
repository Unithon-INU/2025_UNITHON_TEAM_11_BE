package Uniton.Fring.domain.client;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    // 캐시 조회
    List<Recommendation> findByProductIdOrderByRankOrderAsc(Long productId);

    boolean existsByProductIdAndRelatedId(Long productId, Long id);
}
