package Uniton.Fring.domain.like.repository;

import Uniton.Fring.domain.like.entity.MemberLike;
import Uniton.Fring.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberLikeRepository extends JpaRepository<MemberLike, Long> {

    boolean existsByMemberIdAndLikedMemberId(Long memberId, Long likedMemberId);

    void deleteByMemberIdAndLikedMemberId(Long memberId, Long targetMemberId);

    Page<MemberLike> findByMemberId(Long memberId, Pageable pageable);

    @Query("SELECT m FROM MemberLike ml JOIN Member m ON ml.likedMemberId = m.id " +
            "WHERE ml.memberId = :memberId AND m.role = 'CONSUMER'")
    Page<Member> findLikedConsumers(@Param("memberId") Long memberId, Pageable pageable);

    @Query("SELECT m FROM MemberLike ml JOIN Member m ON ml.likedMemberId = m.id " +
            "WHERE ml.memberId = :memberId AND m.role = 'FARMER'")
    Page<Member> findLikedFarmers(@Param("memberId") Long memberId, Pageable pageable);

    @Query("SELECT COUNT(m) FROM MemberLike ml JOIN Member m ON ml.likedMemberId = m.id " +
            "WHERE ml.memberId = :memberId AND m.role = 'CONSUMER'")
    int countLikedConsumers(@Param("memberId") Long memberId);

    @Query("SELECT COUNT(m) FROM MemberLike ml JOIN Member m ON ml.likedMemberId = m.id " +
            "WHERE ml.memberId = :memberId AND m.role = 'FARMER'")
    int countLikedFarmers(@Param("memberId") Long memberId);
}
