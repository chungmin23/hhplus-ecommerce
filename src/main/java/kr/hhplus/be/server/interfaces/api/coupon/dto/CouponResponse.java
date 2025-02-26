package kr.hhplus.be.server.interfaces.api.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponResponse {
    private Long couponId;

    private int discountAmount;

    private boolean isUsed;

    private String name;
}
