package Uniton.Fring.domain.farmer.controller;

import Uniton.Fring.domain.farmer.dto.res.StoreItemsResponseDto;
import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Farmer", description = "농부 전용 페이지 API")
public interface FarmerApiSpecification {

    @Operation(
            summary = "스토어 관리 상품 목록 조회",
            description = "판매자가 등록한 상품 목록을 조회합니다. (최신순 6개)",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "스토어 관리 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StoreItemsResponseDto.class)
                            )
                    )
            }
    )
    ResponseEntity<StoreItemsResponseDto<SimpleProductResponseDto>> getStore(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                             @RequestParam(defaultValue = "0") int page);
}
