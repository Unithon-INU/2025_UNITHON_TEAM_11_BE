package Uniton.Fring.domain.inquiry;

import Uniton.Fring.domain.inquiry.dto.req.InquiryRequestDto;
import Uniton.Fring.domain.inquiry.dto.res.InquiryResponseDto;
import Uniton.Fring.domain.member.entity.Member;
import Uniton.Fring.global.s3.S3Service;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final S3Service s3Service;

    @Transactional
    public InquiryResponseDto inquire(UserDetailsImpl userDetails, Long productId, InquiryRequestDto inquiryRequestDto, List<MultipartFile> images) {

        Member member = userDetails.getMember();

        log.info("[상품 문의 요청] 회원: {}", member.getUsername());

        List<String> imageUrls = s3Service.uploadImages(images, "inquiries");

        Inquiry inquiry = Inquiry.builder().memberId(member.getId()).productId(productId)
                .inquiryRequestDto(inquiryRequestDto).imageUrls(imageUrls).build();

        inquiryRepository.save(inquiry);

        log.info("[상품 문의 성공] 문의: {}", inquiry.getId());

        return InquiryResponseDto.builder().inquiry(inquiry).build();
    }
}
