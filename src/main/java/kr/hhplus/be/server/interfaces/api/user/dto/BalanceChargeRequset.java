package kr.hhplus.be.server.interfaces.api.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceChargeRequset {
    private Long userId;
    private int amount;

}
