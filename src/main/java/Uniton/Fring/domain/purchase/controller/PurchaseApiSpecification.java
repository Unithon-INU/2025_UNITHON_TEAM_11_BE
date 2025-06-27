package Uniton.Fring.domain.purchase.controller;

import Uniton.Fring.domain.purchase.dto.req.PurchaseRequestDto;
import Uniton.Fring.domain.purchase.dto.res.PurchaseResponseDto;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Purchase", description = "구매 관련 API")
public interface PurchaseApiSpecification {

    @Operation(
            summary = "상품 구매",
            description = "상품들을 구매합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "구매 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PurchaseResponseDto.class)
                            )
                    )
            }
    )
    ResponseEntity<PurchaseResponseDto> purchase(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                 @Valid @RequestBody PurchaseRequestDto purchaseRequestDto);

    @Operation(
            summary = "주문 취소",
            description = "주문을 취소합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "주문 취소",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PurchaseResponseDto.class)
                            )
                    )
            }
    )
    ResponseEntity<Void> cancelPurchase(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                        @PathVariable Long purchaseId);
}
