package Uniton.Fring.domain.like.repository;

import Uniton.Fring.domain.like.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

    boolean existsByMemberIdAndReviewId(Long memberId, Long reviewId);

    void deleteByMemberIdAndReviewId(Long memberId, Long reviewId);

    List<ReviewLike> findByMemberIdAndReviewIdIn(Long memberId, List<Long> reviewIds);
}
