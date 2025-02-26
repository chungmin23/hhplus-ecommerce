package kr.hhplus.be.server.interfaces.api.payment.controller;


import io.swagger.v3.oas.annotations.Operation;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentFacade;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.interfaces.api.common.SingleResponseDto;
import kr.hhplus.be.server.interfaces.api.payment.dto.PaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;

    private final PaymentFacade paymentFacade;

    public PaymentController(PaymentService paymentService, PaymentFacade paymentFacade) {
        this.paymentService = paymentService;
        this.paymentFacade = paymentFacade;
    }

    @Operation(summary = "결제 진행", description = "결제을합니다.")
    //결제
    @PostMapping("/api/payment")
    public ResponseEntity payment( @RequestParam Long orderId,
                                   @RequestParam Long userId){
        logger.info("Received request: param1={}, param2={}",  orderId, userId);

        Payment payment = paymentFacade.processPayment(orderId, userId);

        PaymentResponse paymentResponse=  PaymentResponse.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .build();

        logger.info("Sending response: {}", payment);
        return new ResponseEntity<>(new SingleResponseDto<>(paymentResponse), HttpStatus.CREATED);


    }

}
