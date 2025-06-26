package Uniton.Fring.domain.member.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "입점 신청 요청 DTO")
public class ApplyFarmerRequestDto {

    @NotBlank
    @Schema(description = "대표자 이름", example = "홍길동")
    private String name;

    @NotBlank
    @Schema(description = "연락처", example = "010-1234-5678")
    private String phone;

    @NotBlank
    @Schema(description = "마켓 이름", example = "프링마켓")
    private String marketName;

    @NotBlank
    @Schema(description = "우편 주소", example = "서울특별시 송파구 올림픽로")
    private String postalAddress;

    @NotBlank
    @Schema(description = "기본 주소", example = "101동")
    private String defaultAddress;

    @NotBlank
    @Schema(description = "나머지 주소", example = "202호")
    private String restAddress;

    @NotBlank
    @Schema(description = "사업자 등록번호 or 농가 확인 번호", example = "123-45-67890")
    private String RegistNum;

    @NotBlank
    @Schema(description = "계좌번호", example = "234-234234-234234")
    private String bankNum;

    @NotBlank
    @Schema(description = "은행명", example = "농협은행")
    private String bank;

    @NotBlank
    @Schema(description = "마켓 소개글", example = "무농약 채소만 판매합니다.")
    private String intro;
}
