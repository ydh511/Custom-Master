package JuDBu.custommaster.domain.dto.product;

import JuDBu.custommaster.domain.entity.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductUpdateDto {

    private String name;
    private Integer exPrice;

    public static ProductUpdateDto fromEntity(Product entity) {
        return ProductUpdateDto.builder()
                .name(entity.getName())
                .exPrice(entity.getExPrice())
                .build();
    }

    @Override
    public String toString() {
        return "ProductUpdateDto{" +
                "name='" + name + '\'' +
                ", exPrice=" + exPrice +
                '}';
    }
}
