package JuDBu.custommaster.domain.dto.product;

import JuDBu.custommaster.domain.entity.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductReadDto {

    private Long id;
    private String name;
    private Integer price;
    private String image;
    private Integer quantity;

    public static ProductReadDto fromEntity(Product entity) {
        return ProductReadDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getExPrice())
                .image(entity.getExImage())
                .quantity(entity.getQuantity())
                .build();
    }
}
