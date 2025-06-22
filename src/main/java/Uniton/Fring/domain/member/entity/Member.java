package Uniton.Fring.domain.member.entity;

import Uniton.Fring.domain.member.dto.req.MypageRequestDto;
import Uniton.Fring.domain.member.dto.req.SignupRequestDto;
import Uniton.Fring.domain.member.enums.MemberRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private String introduction;

    @Column(nullable = true)
    private String imageUrl;

    @Column(nullable = false)
    private Integer likeCount = 0;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberRole role;

    public Member(SignupRequestDto signupRequestDto, String encodedPassword, String imageUrl) {
        this.email = signupRequestDto.getEmail();
        this.username = signupRequestDto.getUsername();
        this.password = encodedPassword;
        this.nickname = signupRequestDto.getNickname();
        this.introduction = signupRequestDto.getIntroduction();
        this.imageUrl = imageUrl;
        this.role = MemberRole.CONSUMER;
    }

    public void updateMember(MypageRequestDto mypageRequestDto, String imageUrl) {
        this.nickname = mypageRequestDto.getNickname();
        this.introduction = mypageRequestDto.getIntroduction();
        this.imageUrl = imageUrl;
    }

    public void changeRoleToFarmer() {
        this.role = MemberRole.FARMER;
    }

    public void increaseLikeCount() {
        likeCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }
}
