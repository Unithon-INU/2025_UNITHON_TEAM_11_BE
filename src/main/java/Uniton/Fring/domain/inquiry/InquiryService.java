package Uniton.Fring.domain.inquiry;

import Uniton.Fring.domain.inquiry.dto.req.InquiryRequestDto;
import Uniton.Fring.domain.inquiry.dto.res.InquiryResponseDto;
import Uniton.Fring.domain.member.entity.Member;
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

    @Transactional
    public InquiryResponseDto inquire(UserDetailsImpl userDetails, Long productId, InquiryRequestDto inquiryRequestDto, List<MultipartFile> images) {

        Member member = userDetails.getMember();

//        log.info("[상품 문의 요청] 회원: {}", member.getUsername());
//
//
//
//        Inquiry inquiry = Inquiry.builder().memberId(member.getId()).productId(productId)
//                .inquiryRequestDto(inquiryRequestDto).imageUrls().build();
//
//        log.info("[상품 문의 성공]");

        InquiryResponseDto inquiryResponseDto = new InquiryResponseDto();

        return inquiryResponseDto;
    }
}
