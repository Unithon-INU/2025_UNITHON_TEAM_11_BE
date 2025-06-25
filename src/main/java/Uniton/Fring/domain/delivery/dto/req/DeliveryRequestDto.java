package Uniton.Fring.domain.delivery.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "구매 요청 DTO")
public class DeliveryRequestDto {

    @NotBlank(message = "이름이 비어있습니다.")
    @Schema(description = "받는 사람 이름")
    private String name;

    @NotBlank(message = "전화번호 비어있습니다.")
    @Schema(description = "휴대전화 번호")
    private String phoneNumber;

    @NotBlank(message = "우편주소가 비어있습니다.")
    @Schema(description = "우편주소")
    private String zipcode;

    @NotBlank(message = "기본 주소가 비어있습니다.")
    @Schema(description = "기본 주소")
    private String address;

    @NotBlank(message = "상세 주소가 비어있습니다.")
    @Schema(description = "상세 주소")
    private String addressDetail;

    @Schema(description = "배송 요청 사항")
    private String deliveryMessage;
}
