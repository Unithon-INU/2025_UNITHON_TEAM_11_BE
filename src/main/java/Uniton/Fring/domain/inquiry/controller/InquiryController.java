package Uniton.Fring.domain.inquiry.controller;

import Uniton.Fring.domain.inquiry.InquiryService;
import Uniton.Fring.domain.inquiry.dto.req.InquiryRequestDto;
import Uniton.Fring.domain.inquiry.dto.req.ReplyRequestDto;
import Uniton.Fring.domain.inquiry.dto.res.InquiryResponseDto;
import Uniton.Fring.domain.inquiry.dto.res.SimpleInquiryResponseDto;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inquiries")
public class InquiryController implements InquiryApiSpecification {

    private final InquiryService inquiryService;

    // 상품 문의하기
    @PostMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<InquiryResponseDto> inquire(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                         @PathVariable Long productId,
                                                         @RequestPart @Valid InquiryRequestDto inquiryRequestDto,
                                                         @RequestPart("images") List<MultipartFile> images) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inquiryService.inquire(userDetails, productId, inquiryRequestDto, images));
    }

    // 상품 문의 답변하기
    @PostMapping("/reply/{inquiryId}")
    public ResponseEntity<InquiryResponseDto> reply(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                      @PathVariable Long inquiryId,
                                                      @Valid @RequestBody ReplyRequestDto replyRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inquiryService.reply(userDetails, inquiryId, replyRequestDto));
    }

    // 문의 내역 조회
    @GetMapping
    public ResponseEntity<List<SimpleInquiryResponseDto>> getMyInquiries(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                         @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.status(HttpStatus.OK).body(inquiryService.getMyInquiries(userDetails, page));
    }

    // 문의 상세 조회
    @GetMapping("/{inquiryId}")
    public ResponseEntity<InquiryResponseDto> getInquiry(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                         @PathVariable Long inquiryId) {
        return ResponseEntity.status(HttpStatus.OK).body(inquiryService.getInquiry(userDetails, inquiryId));
    }

    // 받은 문의 내역 조회
    @GetMapping("/received")
    public ResponseEntity<List<SimpleInquiryResponseDto>> getReceivedInquiries(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                             @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.status(HttpStatus.OK).body(inquiryService.getReceivedInquiries(userDetails, page));
    }

    // 받은 문의 상세 조회
    @GetMapping("/received/{inquiryId}")
    public ResponseEntity<InquiryResponseDto> getReceivedInquiry(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                             @PathVariable Long inquiryId) {
        return ResponseEntity.status(HttpStatus.OK).body(inquiryService.getReceivedInquiry(userDetails, inquiryId));
    }
}
