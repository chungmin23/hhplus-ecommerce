package kr.hhplus.be.server.interfaces.api.order;


import io.swagger.v3.oas.annotations.Operation;
import kr.hhplus.be.server.interfaces.api.common.MultiResoponseDto;
import kr.hhplus.be.server.interfaces.api.common.SingleResponseDto;
import kr.hhplus.be.server.interfaces.api.order.dto.OrderResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @Operation(summary = "주문 진행", description = "주문을 진행을합니다.")
    //주문
    @PostMapping("/api/order")
    public ResponseEntity order(){
        OrderResponse orderResponse = OrderResponse.builder()
                .orderId(1)
                .discountAmount(23000)
                .totalPrice(5000)
                .status("pending")
                .build();


        return new ResponseEntity<>(new SingleResponseDto<>(orderResponse), HttpStatus.OK);
    }

}
