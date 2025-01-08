package kr.hhplus.be.server.interfaces.api.user.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private int userId;
    private int amount;

}
