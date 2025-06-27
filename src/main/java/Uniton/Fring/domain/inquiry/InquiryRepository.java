package Uniton.Fring.domain.inquiry;

import Uniton.Fring.domain.inquiry.entity.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    Page<Inquiry> findByMemberId(Long memberId, Pageable pageable);

    Page<Inquiry> findByAnswerMemberId(Long answerMemberId, Pageable pageable);
}
