package Uniton.Fring.domain.delivery;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "delivery")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String name;

    // 우편 주소
    @Column(nullable = false)
    private String zipcode;

    // 기본 주소
    @Column(nullable = false)
    private String address;

    // 상세 주소
    @Column(nullable = false)
    private String addressDetail;

    @Column(nullable = false)
    private String phoneNumber;

    private String deliveryMessage;

    @Builder
    private Delivery(Long memberId, String name, String zipcode, String address, String addressDetail, String phoneNumber, String deliveryMessage) {
        this.memberId = memberId;
        this.name = name;
        this.zipcode = zipcode;
        this.address = address;
        this.addressDetail = addressDetail;
        this.phoneNumber = phoneNumber;
        this.deliveryMessage = deliveryMessage;
    }
}
