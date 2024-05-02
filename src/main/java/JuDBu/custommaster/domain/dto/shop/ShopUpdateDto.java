package JuDBu.custommaster.domain.dto.shop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ShopUpdateDto {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String address;

    @Pattern(regexp = "01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$", message = "번호가 틀립니다.")
    private String phoneNumber;
}
