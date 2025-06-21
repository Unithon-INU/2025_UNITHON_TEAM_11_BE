package Uniton.Fring.domain.like;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_like")
public class MemberLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 좋아요를 누른 사람
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    // 좋아요를 받은 사람
    @Column(name = "liked_member_id", nullable = false)
    private Long likedMemberId;
}
