package Uniton.Fring.domain.inquiry;

import Uniton.Fring.domain.inquiry.dto.req.InquiryRequestDto;
import Uniton.Fring.domain.inquiry.dto.res.InquiryResponseDto;
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

    // 상품 문의
    @PostMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<InquiryResponseDto> inquire(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                         @PathVariable Long productId,
                                                         @RequestPart @Valid InquiryRequestDto inquiryRequestDto,
                                                         @RequestPart("images") List<MultipartFile> images) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inquiryService.inquire(userDetails, productId, inquiryRequestDto, images));
    }
}
