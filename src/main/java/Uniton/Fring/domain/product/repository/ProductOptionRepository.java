package Uniton.Fring.domain.product.repository;

import Uniton.Fring.domain.product.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {

    List<ProductOption> findByProductId(Long productId);

    Optional<ProductOption> findByProductIdAndOptionName(Long productId, String optionName);

    void deleteByProductId(Long productId);
}
