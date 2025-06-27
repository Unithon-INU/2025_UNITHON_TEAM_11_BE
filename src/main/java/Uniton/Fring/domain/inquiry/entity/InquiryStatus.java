package Uniton.Fring.domain.inquiry.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InquiryStatus {
    UNANSWERED("미답변"),
    ANSWERED("답변완료");

    private final String description;
}
