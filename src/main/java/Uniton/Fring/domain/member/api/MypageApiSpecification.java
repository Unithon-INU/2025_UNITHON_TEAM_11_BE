package Uniton.Fring.domain.member.api;

import Uniton.Fring.domain.member.dto.req.MypageRequestDto;
import Uniton.Fring.domain.member.dto.res.MypageResponseDto;
import Uniton.Fring.domain.order.SimpleOrderResponseDto;
import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Mypage", description = "마이페이지 관련 API")
public interface MypageApiSpecification {

    @Operation(
            summary = "마이페이지 조회",
            description = "회원의 마이페이지 정보를 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "마이페이지 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MypageResponseDto.class)
                            )
                    )
            }
    )
    ResponseEntity<MypageResponseDto> getMypage(@AuthenticationPrincipal UserDetailsImpl userDetails);

    @Operation(
            summary = "마이페이지 수정",
            description = "회원의 마이페이지 정보를 수정합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "마이페이지 수정 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MypageResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "파일 변환에 실패했습니다.")
            }
    )
    ResponseEntity<MypageResponseDto> updateMypage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                   @RequestPart @Valid MypageRequestDto mypageRequestDto,
                                                   @RequestPart(value = "image", required = false) MultipartFile image);

    @Operation(
            summary = "주문 내역",
            description = "회원의 주문 내역을 반환합니다. (한 번에 4개)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "주문 내역 반환 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleOrderResponseDto.class, type = "array")
                            )
                    )
            }
    )
    ResponseEntity<List<SimpleOrderResponseDto>> getOrderHistory(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                 @RequestParam(defaultValue = "0") int page);

    @Operation(
            summary = "최근 본 상품",
            description = "회원이 최근 본 상품들을 반환합니다. (한 번에 6개)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "최근 본 상품들 반환 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleProductResponseDto.class, type = "array")
                            )
                    )
            }
    )
    ResponseEntity<List<SimpleProductResponseDto>> getRecentViewedProducts(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                           @RequestParam(defaultValue = "0") int page);
}
