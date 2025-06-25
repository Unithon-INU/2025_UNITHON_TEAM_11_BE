package Uniton.Fring.domain.delivery;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Optional<Delivery> findByMemberIdAndZipcodeAndAddressAndAddressDetail(
            Long memberId, String zipcode, String address, String addressDetail);
}
