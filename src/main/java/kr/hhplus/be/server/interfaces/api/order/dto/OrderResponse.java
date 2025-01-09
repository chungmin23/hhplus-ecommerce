package kr.hhplus.be.server.interfaces.api.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private int orderId;

    private String status;

    private int totalPrice;

    private int discountAmount;
}
