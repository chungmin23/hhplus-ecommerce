package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.product.Product;
import lombok.*;
import org.springframework.web.bind.annotation.RequestParam;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailRequest {
    private Long productId;
    private int quantity;





}
