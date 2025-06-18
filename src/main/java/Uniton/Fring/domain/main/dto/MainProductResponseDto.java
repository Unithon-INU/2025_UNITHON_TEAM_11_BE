package Uniton.Fring.domain.main.dto;

import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "상품 메인 페이지 응답 DTO")
public class MainProductResponseDto {

    @Schema(description = "평점 상위 상품 목록", example = "[" +
            "{\"id\": 1, \"name\": \"신선한 사과\", \"price\": 10000, \"salePrice\": 8000, \"image\": \"https://your-bucket.s3.ap-northeast-2.amazonaws.com/products/apple.jpg\", \"isLiked\": true}," +
            "{\"id\": 2, \"name\": \"달콤한 딸기\", \"price\": 12000, \"salePrice\": 9600, \"image\": \"https://your-bucket.s3.ap-northeast-2.amazonaws.com/products/strawberry.jpg\", \"isLiked\": false}" +
            "]")
    private final List<SimpleProductResponseDto> bestProductResponseDtos;

    @Schema(description = "리뷰 많은 상품 목록", example = "[" +
            "{\"id\": 3, \"name\": \"브로콜리 한 봉지\", \"price\": 6000, \"salePrice\": 5400, \"image\": \"https://your-bucket.s3.ap-northeast-2.amazonaws.com/products/broccoli.jpg\", \"isLiked\": false}," +
            "{\"id\": 4, \"name\": \"유기농 당근\", \"price\": 8000, \"salePrice\": 7200, \"image\": \"https://your-bucket.s3.ap-northeast-2.amazonaws.com/products/carrot.jpg\", \"isLiked\": true}" +
            "]")
    private final List<SimpleProductResponseDto> frequentProductResponseDtos;
}
