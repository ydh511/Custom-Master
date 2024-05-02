package JuDBu.custommaster.domain.dto.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCreateDto {

    private String name;
    private Integer exPrice;

    @Override
    public String toString() {
        return "ProductCreateDto{" +
                "name='" + name + '\'' +
                ", exPrice=" + exPrice +
                '}';
    }
}
