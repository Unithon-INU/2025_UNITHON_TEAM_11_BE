package Uniton.Fring.domain.farmer.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "마이페이지 수정 요청 DTO")
public class UpdateStoreRequestDto {

    @Schema(description = "회원 닉네임", example = "두옹균")
    private String nickname;

    @Schema(description = "회원 소개글", example = "핑크솔트로 구운 삼겹살을 좋아해요")
    private String introduction;

    @Schema(description = "대표자 이름", example = "김프링")
    private String representativeName;

    @Schema(description = "연락처", example = "010-1234-5678")
    private String phoneNumber;

    @Schema(description = "사업자 등록번호 혹은 농가확인번호", example = "12345678910")
    private String regisNum;

    @Schema(description = "우편번호", example = "12345")
    private String zipcode;

    @Schema(description = "기본 주소지", example = "경기도 프링시 프링구 프링동")
    private String address;

    @Schema(description = "상세 주소지", example = "101동 123호")
    private String addressDetail;
}
