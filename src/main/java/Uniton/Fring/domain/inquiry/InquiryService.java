package Uniton.Fring.domain.inquiry;

import Uniton.Fring.domain.inquiry.dto.req.InquiryRequestDto;
import Uniton.Fring.domain.inquiry.dto.res.InquiryResponseDto;
import Uniton.Fring.domain.inquiry.dto.res.SimpleInquiryResponseDto;
import Uniton.Fring.domain.inquiry.entity.Inquiry;
import Uniton.Fring.domain.member.entity.Member;
import Uniton.Fring.global.exception.CustomException;
import Uniton.Fring.global.exception.ErrorCode;
import Uniton.Fring.global.s3.S3Service;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Transactional(readOnly = true)
    public List<SimpleInquiryResponseDto> getMyInquiries(UserDetailsImpl userDetails, int page) {

        Member member = userDetails.getMember();
        log.info("[나의 문의 내역 조회 요청], 회원: {}", member.getUsername());

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Inquiry> inquiries = inquiryRepository.findByMemberId(member.getId(), pageable);

        List<SimpleInquiryResponseDto> inquiryResponseDtos = inquiries.stream()
                .map(inquiry -> SimpleInquiryResponseDto.builder().inquiry(inquiry).build()).toList();

        log.info("[나의 문의 내역 조회 성공]");

        return inquiryResponseDtos;
    }

    public InquiryResponseDto getInquiry(UserDetailsImpl userDetails, Long inquiryId) {

        Member member = userDetails.getMember();
        log.info("[문의 상세 조회 요청], 회원: {}", member.getUsername());

        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> {
                    log.warn("[문의 정보 조회 실패] 문의 없음: inquiryId={}", inquiryId);
                    return new CustomException(ErrorCode.INQUIRY_NOT_FOUND);
                });

        if (!inquiry.getMemberId().equals(member.getId())) {
            log.warn("[문의 정보 조회 실패] 권한 없음: member={}, inquiryMemberId={}", member.getUsername(), inquiry.getMemberId());
            throw new CustomException(ErrorCode.INQUIRY_MEMBER_NOT_MATCH);
        }

        InquiryResponseDto inquiryResponseDto = InquiryResponseDto.builder().inquiry(inquiry).build();

        log.info("[문의 상세 조회 성공]");

        return inquiryResponseDto;
    }
}
