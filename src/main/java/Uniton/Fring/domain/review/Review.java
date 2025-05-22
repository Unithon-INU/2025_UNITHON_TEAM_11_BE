package Uniton.Fring.domain.review;

import Uniton.Fring.domain.farmProduct.FarmProduct;
import Uniton.Fring.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private FarmProduct product;

    private int rating;

    private String content;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}