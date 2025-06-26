package Uniton.Fring.domain.farmer.dto.res;

import Uniton.Fring.domain.farmer.Farmer;
import Uniton.Fring.domain.member.entity.Member;
import Uniton.Fring.domain.member.enums.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "마이페이지 정보 응답 DTO")
public class StoreResponseDto {

    @Schema(description = "회원 Id", example = "1")
    private final Long memberId;

    @Schema(description = "회원 프로필 이미지 URL", example = "http://example.com/profile.jpg")
    private final String imageUrl;

    @Schema(description = "회원 닉네임", example = "핑크솔트123")
    private final String nickname;

    @Schema(description = "회원 소개글", example = "핑크솔트로 구운 삼겹살을 좋아해요")
    private final String introduction;

    @Schema(description = "판매자 여부", example = "true")
    private final Boolean isSeller;

    @Schema(description = "대표자 이름", example = "김프링")
    private final String representativeName;

    @Schema(description = "연락처", example = "010-1234-5678")
    private final String phoneNumber;

    @Schema(description = "사업자 등록번호 혹은 농가확인번호", example = "12345678910")
    private final String registrationNumber;

    @Schema(description = "우편번호", example = "12345")
    private final String zipcode;

    @Schema(description = "기본 주소지", example = "경기도 프링시 프링구 프링동")
    private final String address;

    @Schema(description = "상세 주소지", example = "101동 123호")
    private final String addressDetail;

    @Builder
    private StoreResponseDto(Member member, Farmer farmer) {
        this.memberId = member.getId();
        this.imageUrl = member.getImageUrl();
        this.nickname = member.getNickname();
        this.introduction = member.getIntroduction();
        this.isSeller = member.getRole() == MemberRole.FARMER;
        this.representativeName = farmer.getRepresentativeName();
        this.phoneNumber = farmer.getPhoneNumber();
        this.registrationNumber = farmer.getRegisNum();
        this.zipcode = farmer.getPostalAddress();
        this.address = farmer.getDefaultAddress();
        this.addressDetail = farmer.getRestAddress();
    }
}
