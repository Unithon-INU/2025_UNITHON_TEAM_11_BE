package Uniton.Fring.domain.client;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recommendation")
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "related_id", nullable = false)
    private Long relatedId;

    @Column(nullable = false)
    private int rankOrder;

    @Builder
    private Recommendation(Long productId, Long relatedId, int rankOrder) {
        this.productId  = productId;
        this.relatedId  = relatedId;
        this.rankOrder  = rankOrder;
    }
}
