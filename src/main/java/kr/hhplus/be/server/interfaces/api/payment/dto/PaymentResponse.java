package kr.hhplus.be.server.interfaces.api.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private int paymentId;

    private int orderId;

    private String status;

    private int amount;

}
