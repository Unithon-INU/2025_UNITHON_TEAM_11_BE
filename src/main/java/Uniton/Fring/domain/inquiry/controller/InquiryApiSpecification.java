package Uniton.Fring.domain.inquiry.controller;

import Uniton.Fring.domain.inquiry.dto.req.InquiryRequestDto;
import Uniton.Fring.domain.inquiry.dto.res.InquiryResponseDto;
import Uniton.Fring.domain.inquiry.dto.res.SimpleInquiryResponseDto;
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

@Tag(name = "Inquiry", description = "회원 인증 및 권한 관련 API")
public interface InquiryApiSpecification {

    @Operation(
            summary = "상품 문의하기",
            description = "상품에 대한 문의를 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "201",
                            description = "상품 문의 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = InquiryResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "파일 변환에 실패했습니다.")
            }
    )
    ResponseEntity<InquiryResponseDto> inquire(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                               @PathVariable Long productId,
                                               @RequestPart @Valid InquiryRequestDto inquiryRequestDto,
                                               @RequestPart("images") List<MultipartFile> images);

    @Operation(
            summary = "나의 문의 내역 조회",
            description = "회원 본인의 문의 내역을 조회합니다. (한 번에 10개)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "나의 문의 내역 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = InquiryResponseDto.class, type = "array")
                            )
                    )
            }
    )
    ResponseEntity<List<SimpleInquiryResponseDto>> getMyInquiries(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                  @RequestParam(defaultValue = "0") int page);


}
