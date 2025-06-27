package Uniton.Fring.domain.inquiry.entity;

import Uniton.Fring.domain.inquiry.dto.req.InquiryRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "inquiry")
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ElementCollection
    @CollectionTable(name = "inquiry_image", joinColumns = @JoinColumn(name = "inquiry_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;

    @Enumerated(EnumType.STRING)
    private InquiryStatus status = InquiryStatus.UNANSWERED;

    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @Builder
    private Inquiry(Long memberId, Long productId, InquiryRequestDto inquiryRequestDto, List<String> imageUrls) {
        this.memberId = memberId;
        this.productId = productId;
        this.title = inquiryRequestDto.getTitle();
        this.content = inquiryRequestDto.getContent();
        this.imageUrls = imageUrls;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDate.now();
    }
}
