package Uniton.Fring.domain.like.repository;

import Uniton.Fring.domain.like.entity.MemberLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberLikeRepository extends JpaRepository<MemberLike, Long> {

    boolean existsByMemberIdAndLikedMemberId(Long memberId, Long likedMemberId);

    void deleteByMemberIdAndLikedMemberId(Long memberId, Long targetMemberId);

    int countByLikedMemberId(Long targetMemberId);
}
