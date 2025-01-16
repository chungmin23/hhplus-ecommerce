package kr.hhplus.be.server.interfaces.api.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.interfaces.api.common.SingleResponseDto;
import kr.hhplus.be.server.interfaces.api.payment.controller.PaymentController;
import kr.hhplus.be.server.interfaces.api.user.dto.BalanceChargeRequset;
import kr.hhplus.be.server.interfaces.api.user.dto.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    @Autowired
    private UserService userService;

    @Operation(summary = "잔액 조회", description = "잔액 조회를 합니다.")
    //잔액 조회
    @GetMapping("/balance/{userId}")
    public ResponseEntity getBalance(@PathVariable Long userId){
        logger.info("Received request: param1={}",  userId);

        User  user = userService.getBalance(userId);
        UserResponse userResponse = UserResponse.builder()
                .userId(user.getId())
                .amount(user.getBalance())
                .build();

        logger.info("Sending response: {}", user);
        return new ResponseEntity<>(new SingleResponseDto<>(userResponse), HttpStatus.OK);

    }

    @Operation(summary = "잔액 충전", description = "잔액 충전을합니다.")
    //잔액 충전
    @PostMapping("/balance")
    public ResponseEntity chargeBalance(@RequestBody BalanceChargeRequset requset){
        logger.info("Received request: param1={}",  requset);

       User user =  userService.chargeBalance(requset.getUserId(), requset.getAmount());

        logger.info("Sending response: {}", user);
        return new ResponseEntity<>(new SingleResponseDto<>(user), HttpStatus.CREATED);
    }

}
