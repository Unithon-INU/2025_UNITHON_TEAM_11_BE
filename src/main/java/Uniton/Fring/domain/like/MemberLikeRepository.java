package Uniton.Fring.domain.like;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberLikeRepository extends JpaRepository<MemberLike, Long> {

    boolean existsByMemberIdAndLikedMemberId(Long memberId, Long likedMemberId);
}
