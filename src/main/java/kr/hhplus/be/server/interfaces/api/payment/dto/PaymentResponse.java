package kr.hhplus.be.server.interfaces.api.payment.dto;

import kr.hhplus.be.server.domain.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private Long id;

    private int orderId;

    private PaymentStatus status;

    private int amount;

}
