package Uniton.Fring.domain.farmer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FarmerRepository extends JpaRepository<Farmer, Long> {

    Optional<Farmer> findByMemberId(Long memberId);

    boolean existsByRegisNum(String regisNum);
}
