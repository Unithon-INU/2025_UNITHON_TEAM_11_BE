package Uniton.Fring.domain.inquiry;

import Uniton.Fring.domain.inquiry.dto.req.InquiryRequestDto;
import Uniton.Fring.domain.inquiry.dto.res.AnswerResponseDto;
import Uniton.Fring.domain.inquiry.dto.res.InquiryResponseDto;
import Uniton.Fring.domain.inquiry.dto.res.SimpleInquiryResponseDto;
import Uniton.Fring.domain.inquiry.entity.Inquiry;
import Uniton.Fring.domain.inquiry.entity.InquiryStatus;
import Uniton.Fring.domain.member.entity.Member;
import Uniton.Fring.domain.member.repository.MemberRepository;
import Uniton.Fring.domain.product.entity.Product;
import Uniton.Fring.domain.product.repository.ProductRepository;
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
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final S3Service s3Service;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public InquiryResponseDto inquire(UserDetailsImpl userDetails, Long productId, InquiryRequestDto inquiryRequestDto, List<MultipartFile> images) {

        Member member = userDetails.getMember();

        log.info("[상품 문의 요청] 회원: {}", member.getUsername());

        List<String> imageUrls = s3Service.uploadImages(images, "inquiries");

        Inquiry inquiry = Inquiry.builder().memberId(member.getId()).productId(productId)
                .inquiryRequestDto(inquiryRequestDto).imageUrls(imageUrls).build();

        inquiryRepository.save(inquiry);

        log.info("[상품 문의 성공] 문의: {}", inquiry.getId());

        return InquiryResponseDto.builder().inquiry(inquiry).memberNickname(member.getNickname()).productImageUrl(null).answer(null).build();
    }

    @Transactional(readOnly = true)
    public List<SimpleInquiryResponseDto> getMyInquiries(UserDetailsImpl userDetails, int page) {

        Member member = userDetails.getMember();
        log.info("[나의 문의 내역 조회 요청], 회원: {}", member.getUsername());

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Inquiry> inquiryPage = inquiryRepository.findByMemberId(member.getId(), pageable);
        List<Inquiry> inquiries = inquiryPage.getContent();

        if (inquiries.isEmpty()) {
            log.info("[나의 문의 내역 조회] 결과 없음");
            return List.of();
        }

        List<Long> productIds = inquiries.stream().map(Inquiry::getProductId).distinct().toList();

        Map<Long, String> productImageMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Product::getMainImageUrl));

        List<SimpleInquiryResponseDto> inquiryResponseDtos = inquiries.stream()
                .map(inquiry ->
                    SimpleInquiryResponseDto.builder()
                            .inquiry(inquiry).imageUrl(productImageMap.get(inquiry.getProductId())).build()
                ).toList();

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

        Product product = productRepository.findById(inquiry.getProductId())
                .orElseThrow(() -> {
                    log.warn("[상품 정보 조회 실패] 상품 없음: productId={}", inquiry.getProductId());
                    return new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
                });

        AnswerResponseDto answerResponseDto = null;
        if (inquiry.getStatus() == InquiryStatus.ANSWERED) {
            Member answeredMember = memberRepository.findById(inquiry.getAnswerMemberId())
                    .orElseThrow(() -> {
                        log.warn("[답변 회원 정보 조회 실패] 회원 없음: memberId={}", inquiry.getAnswerMemberId());
                        return new CustomException(ErrorCode.MEMBER_NOT_FOUND);
                    });
            answerResponseDto = AnswerResponseDto.builder().inquiry(inquiry).member(answeredMember).build();
        }

        InquiryResponseDto inquiryResponseDto = InquiryResponseDto.builder()
                .inquiry(inquiry).memberNickname(member.getNickname())
                .productImageUrl(product.getMainImageUrl()).answer(answerResponseDto).build();

        log.info("[문의 상세 조회 성공]");

        return inquiryResponseDto;
    }
}
