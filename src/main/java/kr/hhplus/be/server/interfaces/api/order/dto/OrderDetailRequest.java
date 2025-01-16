package kr.hhplus.be.server.interfaces.api.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailRequest {
    private Long orderId;

    private Long userId;

    private String status;

    private int totalPrice;

    private int discountAmount;
}
