package Uniton.Fring.domain.cart.controller;


import Uniton.Fring.domain.cart.dto.req.ProductItemRequestDto;
import Uniton.Fring.domain.cart.dto.req.CartRequestDto;
import Uniton.Fring.domain.cart.dto.res.CartInfoResponseDto;
import Uniton.Fring.domain.cart.dto.res.ItemResponseDto;
import Uniton.Fring.domain.cart.dto.res.CartUpdateResponseDto;
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
            summary = "장바구니 조회",
            description = "회원의 장바구니 목록을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "장바구니 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CartInfoResponseDto.class)
                            )
                    )
            }
    )
    ResponseEntity<CartInfoResponseDto> getCart(@AuthenticationPrincipal UserDetailsImpl userDetails);

    @Operation(
            summary = "장바구니 추가",
            description = "상품 아이디를 통해 회원의 장바구니에 추가합니다. <br><br>이미 있는 항목을 추가할 경우 해당 항목의 개수가 증가합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "장바구니 추가 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ItemResponseDto.class)
                            )
                    )
            }
    )
    ResponseEntity<ItemResponseDto> addCart(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @RequestBody ProductItemRequestDto productItemRequestDto);

    @Operation(
            summary = "장바구니 수정",
            description = "회원의 장바구니 목록을 수정합니다. (장바구니에서 나갈 때의 api입니다.) <br><br>회원의 장바구니 목록을 전부 삭제하고 새로운 값으로 채웁니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "장바구니 수정 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CartUpdateResponseDto.class)
                            )
                    )
            }
    )
    ResponseEntity<CartUpdateResponseDto> updateCart(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @RequestBody CartRequestDto cartRequestDto);

    @Operation(
            summary = "장바구니 전체 삭제",
            description = "회원의 장바구니 목록 전체를 삭제합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "장바구니 삭제 성공"
                    )
            }
    )
    ResponseEntity<CartInfoResponseDto> deleteCart(@AuthenticationPrincipal UserDetailsImpl userDetails);
}
