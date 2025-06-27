package Uniton.Fring.domain.member.api;

import Uniton.Fring.domain.farmer.dto.res.StoreResponseDto;
import Uniton.Fring.domain.member.dto.req.ApplyFarmerRequestDto;
import Uniton.Fring.domain.member.dto.req.MypageRequestDto;
import Uniton.Fring.domain.member.dto.res.MypageDetailResponseDto;
import Uniton.Fring.domain.member.dto.res.MypageResponseDto;
import Uniton.Fring.domain.member.dto.res.MypageReviewResponseDto;
import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.domain.purchase.dto.res.SimplePurchaseResponseDto;
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
            summary = "마이페이지 상세 조회",
            description = "회원의 마이페이지 상세 정보를 조회합니다. <br><br>레시피는 최신순 정렬 기본 페이지 6개",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "마이페이지 상세 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MypageDetailResponseDto.class)
                            )
                    )
            }
    )
    ResponseEntity<MypageDetailResponseDto> getDetailMypage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                            @RequestParam(defaultValue = "0") int page);

    @Operation(
            summary = "마이페이지 수정",
            description = "회원의 마이페이지 정보를 수정합니다. <br><br> 값이 비어있는 필드에 대해선 이전 값이 적용됩니다.",
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
                                    schema = @Schema(implementation = SimplePurchaseResponseDto.class, type = "array")
                            )
                    )
            }
    )
    ResponseEntity<List<SimplePurchaseResponseDto>> getPurchaseHistory(@AuthenticationPrincipal UserDetailsImpl userDetails,
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

    @Operation(
            summary = "나의 리뷰 내역 조회",
            description = "회원 본인의 리뷰 내역을 조회합니다. (한 번에 5개) <br><br>상품 리뷰와 레시피 리뷰를 분리해서 전달합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "나의 리뷰 내역 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MypageReviewResponseDto.class)
                            )
                    )
            }
    )
    ResponseEntity<MypageReviewResponseDto> getMyReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                        @RequestParam(defaultValue = "0") int page);

    @Operation(
            summary = "입점 신청",
            description = "소비자 회원에 대한 입점 신청입니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "입점 신청 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StoreResponseDto.class)
                            )
                    )
            }
    )
    ResponseEntity<StoreResponseDto> applyFarmer(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                 @RequestPart @Valid ApplyFarmerRequestDto applyFarmerRequestDto,
                                                 @RequestPart(value = "RegistFile", required = false) MultipartFile registFile,
                                                 @RequestPart(value = "Passbook", required = false) MultipartFile passbook,
                                                 @RequestPart(value = "certifidoc", required = false) MultipartFile certifidoc,
                                                 @RequestPart(value = "profile", required = false) MultipartFile profile);

    @Operation(
            summary = "회원 등록 번호 중복 확인",
            description = "사업자 등록번호 or 농가확인번호 중복 확인 (true = 중복 X, 즉 사용 가능하다)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원 등록 번호 중복 확인 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Boolean.class)
                            )
                    )
            }
    )
    ResponseEntity<Boolean> checkRegistNumDuplicated(@PathVariable String registNum);
}
