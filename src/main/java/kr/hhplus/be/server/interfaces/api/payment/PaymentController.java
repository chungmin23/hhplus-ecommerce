package kr.hhplus.be.server.interfaces.api.payment;


import io.swagger.v3.oas.annotations.Operation;
import kr.hhplus.be.server.interfaces.api.common.MultiResoponseDto;
import kr.hhplus.be.server.interfaces.api.common.SingleResponseDto;
import kr.hhplus.be.server.interfaces.api.payment.dto.PaymentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    @Operation(summary = "결제 진행", description = "결제을합니다.")
    //결제
    @PostMapping("/api/payment")
    public ResponseEntity payment(){
        PaymentResponse paymentResponse = PaymentResponse.builder()
                .paymentId(1)
                .orderId(2)
                .amount(23000)
                .status("success")
                .build();


        return new ResponseEntity<>(new SingleResponseDto<>(paymentResponse), HttpStatus.CREATED);


    }

}
