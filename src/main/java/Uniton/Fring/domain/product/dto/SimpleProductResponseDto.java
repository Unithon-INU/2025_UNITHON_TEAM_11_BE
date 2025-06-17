package Uniton.Fring.domain.product.dto;

import Uniton.Fring.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class SimpleProductResponseDto {

    private final Long productId;
    private final String imageUrl;
    private final String name;
    private final BigDecimal price;

    @Builder
    public SimpleProductResponseDto(Product product) {
        this.productId = product.getId();
        this.imageUrl = product.getImageUrl();
        this.name = product.getName();
        this.price = product.getPrice();
    }
}
