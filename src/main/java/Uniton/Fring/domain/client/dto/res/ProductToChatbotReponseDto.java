package Uniton.Fring.domain.client.dto.res;

import Uniton.Fring.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "챗봇 전달용 상품 정보 응답 DTO")
public class ProductToChatbotReponseDto {

    @Schema(description = "상품 설명", example = "신선한 사과구요....")
    private final String description;

    @Builder
    private ProductToChatbotReponseDto(Product product) {
        this.description = product.getDescription();
    }
}
