package JuDBu.custommaster.domain.dto.payment;

import lombok.Data;

@Data
public class PaymentConfirmDto {
    private String paymentKey;
    private String orderId;
    private Integer amount;
}
