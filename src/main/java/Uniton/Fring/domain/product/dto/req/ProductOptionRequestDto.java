package Uniton.Fring.domain.product.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "상품 옵션 요청 DTO")
public class ProductOptionRequestDto {

    @Schema(description = "옵션 이름", example = "계란 10구 추가")
    private String optionName;

    @Schema(description = "추가 금액", example = "2000")
    private Integer additionalPrice;
}