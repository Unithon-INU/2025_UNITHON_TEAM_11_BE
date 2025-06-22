package Uniton.Fring.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recent_product_view")
public class RecentProductView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "viewed_at", nullable = false)
    private LocalDateTime viewedAt;

    public RecentProductView(Long memberId, Long productId) {
        this.memberId = memberId;
        this.productId = productId;
        this.viewedAt = LocalDateTime.now();
    }

    public void updateViewTime() {
        this.viewedAt = LocalDateTime.now();
    }
}
