package JuDBu.custommaster.domain.dto.ord;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrdRequestDto {

    @NotBlank
    private String name;

    @Pattern(regexp = "01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$", message = "번호가 틀립니다.")
    private String phoneNumber;

    @Future(message = "미래의 날짜로 입력해주세요.")
    private LocalDateTime pickupDate; // 픽업 날짜

    @NotBlank
    private String Requirements; // 요구사항

    @Override
    public String toString() {
        return "OrdRequestDto{" +
                "name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", pickupDate='" + pickupDate + '\'' +
                ", Requirements='" + Requirements + '\'' +
                '}';
    }

}
