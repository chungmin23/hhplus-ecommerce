package kr.hhplus.be.server.interfaces.api.user.controller;

import io.swagger.v3.oas.annotations.Operation;
//import kr.hhplus.be.server.domain.user.User;
//import kr.hhplus.be.server.interfaces.api.common.MultiResoponseDto;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import kr.hhplus.be.server.interfaces.api.common.SingleResponseDto;
import kr.hhplus.be.server.interfaces.api.user.dto.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {


    @Operation(summary = "잔액 조회", description = "잔액 조회를 합니다.")
//    @ApiResponse(responseCode = "200", description = "잔액 조회 성공",
//            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)))
    //잔액 조회
    @GetMapping("/balnace/{userId}")
    public ResponseEntity getBalance(@PathVariable int userId){
        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(1);
        userResponse.setAmount(10000);


        return new ResponseEntity<>(new SingleResponseDto<>(userResponse), HttpStatus.OK);

    }

    @Operation(summary = "잔액 충전", description = "잔액 충전을합니다.")
    //잔액 충전
    @PostMapping("/balance")
    public ResponseEntity chargeBalance(){
        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(1);
        userResponse.setAmount(10000);


        return new ResponseEntity<>(new SingleResponseDto<>(userResponse), HttpStatus.CREATED);
    }

}
