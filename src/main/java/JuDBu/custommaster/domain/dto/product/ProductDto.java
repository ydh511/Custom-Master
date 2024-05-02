package JuDBu.custommaster.domain.dto.product;

import JuDBu.custommaster.domain.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProductDto {

    private Long id;
    private Long shopId;
    private String name;
    private Integer exPrice;
    private Integer quantity;
    private Integer exImage;

    public static ProductDto fromEntity(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .shopId(product.getShop().getId())
                .name(product.getName())
                .exPrice(product.getExPrice())
                .quantity(product.getQuantity())
                //.exImage(product.getExImage())
                .build();
    }

    @Override
    public String toString() {
        return "ProductDto{" +
                "id=" + id +
                ", shopId=" + shopId +
                ", name='" + name + '\'' +
                ", exPrice=" + exPrice +
                ", quantity=" + quantity +
                ", exImage=" + exImage +
                '}';
    }
}
