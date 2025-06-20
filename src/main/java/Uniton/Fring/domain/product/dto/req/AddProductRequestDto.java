package Uniton.Fring.domain.product.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "상품 정보 수정 요청 DTO ")
public class AddProductRequestDto {


    @NotBlank(message = "인증 번호가 비어있습니다.")
    @Schema(description = "사용자의 인증번호", example = "562934")
    private String authNum;
}
