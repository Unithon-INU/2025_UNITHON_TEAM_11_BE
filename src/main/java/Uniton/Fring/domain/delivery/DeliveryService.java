package Uniton.Fring.domain.delivery;

import Uniton.Fring.domain.delivery.dto.req.DeliveryRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    @Transactional
    public Delivery saveOrGet(DeliveryRequestDto dto, Long memberId) {

        // 동일 배송지 (우편번호+기본주소+상세주소)가 이미 있는지 조회
        Optional<Delivery> sameAddress = deliveryRepository
                .findByMemberIdAndZipcodeAndAddressAndAddressDetail(memberId, dto.getZipcode(), dto.getAddress(), dto.getAddressDetail());

        if (sameAddress.isPresent()) {
            return sameAddress.get();
        }

        // 없으면 새로 저장
        Delivery delivery = Delivery.builder()
                .memberId(memberId)
                .name(dto.getName())
                .zipcode(dto.getZipcode())
                .address(dto.getAddress())
                .addressDetail(dto.getAddressDetail())
                .phoneNumber(dto.getPhoneNumber())
                .deliveryMessage(dto.getDeliveryMessage())
                .build();

        return deliveryRepository.save(delivery);
    }
}
