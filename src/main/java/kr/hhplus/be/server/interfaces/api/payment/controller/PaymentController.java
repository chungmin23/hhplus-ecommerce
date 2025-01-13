package kr.hhplus.be.server.interfaces.api.payment.controller;


import io.swagger.v3.oas.annotations.Operation;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.interfaces.api.common.MultiResoponseDto;
import kr.hhplus.be.server.interfaces.api.common.SingleResponseDto;
import kr.hhplus.be.server.interfaces.api.payment.dto.PaymentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "결제 진행", description = "결제을합니다.")
    //결제
    @PostMapping("/api/payment")
    public ResponseEntity payment( @RequestParam Long orderId,
                                   @RequestParam Long userId){
        Payment payment = paymentService.processPayment(orderId, userId);


        return new ResponseEntity<>(new SingleResponseDto<>(payment), HttpStatus.CREATED);


    }

}
