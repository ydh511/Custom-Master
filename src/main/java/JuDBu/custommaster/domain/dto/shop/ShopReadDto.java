package JuDBu.custommaster.domain.dto.shop;

import JuDBu.custommaster.domain.dto.product.ProductReadDto;
import JuDBu.custommaster.domain.entity.Shop;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class ShopReadDto {

    private Long id;
    private String name;
    private String address;
    private String phoneNumber;
    private List<ProductReadDto> productReadDtos = new ArrayList<>();

    public static ShopReadDto fromEntity(Shop entity) {
        return ShopReadDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .address(entity.getAddress())
                .phoneNumber(entity.getPhoneNumber())
                .productReadDtos(
                        entity.getProducts().stream()
                                .map(ProductReadDto::fromEntity)
                                .toList())
                .build();
    }
}
