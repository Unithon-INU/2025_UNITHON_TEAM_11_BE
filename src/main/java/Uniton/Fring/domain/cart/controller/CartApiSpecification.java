package Uniton.Fring.domain.cart.controller;


import Uniton.Fring.domain.cart.dto.res.AddCartResponseDto;
import Uniton.Fring.domain.cart.dto.req.AddCartRequestDto;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Cart", description = "장바구니 관련 API")
public interface CartApiSpecification {

    @Operation(
            summary = "장바구니 추가",
            description = "상품 ID를 받아 해당 게임을 현재 로그인한 사용자의 장바구니에 추가합니다.",
            responses = {
                    // 201 Created 응답일 때
                    @ApiResponse(
                            responseCode = "201",
                            description = "장바구니에 성공적으로 추가됨",
                            // 응답 본문은 JSON 형식이고, 구조는 CartResponse DTO를 따름
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AddCartResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
            }
    )
    public ResponseEntity<AddCartResponseDto> addCartItems(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody AddCartRequestDto addCartRequestDto);
}
