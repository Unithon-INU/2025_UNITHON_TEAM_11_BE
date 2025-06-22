package Uniton.Fring.domain.like.repository;

import Uniton.Fring.domain.like.entity.MemberLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberLikeRepository extends JpaRepository<MemberLike, Long> {

    boolean existsByMemberIdAndLikedMemberId(Long memberId, Long likedMemberId);

    void deleteByMemberIdAndLikedMemberId(Long memberId, Long targetMemberId);

    List<MemberLike> findByMemberIdAndLikedMemberIdIn(Long memberId, List<Long> likedMemberIds);
}
