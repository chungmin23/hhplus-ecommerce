package kr.hhplus.be.server.interfaces.api.coupon.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.hhplus.be.server.interfaces.api.common.MultiResoponseDto;
import kr.hhplus.be.server.interfaces.api.common.SingleResponseDto;
import kr.hhplus.be.server.interfaces.api.coupon.dto.CouponResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CouponController {

    @Operation(summary = "쿠폰 발급", description = "쿠폰 발급을합니다.")
    //쿠폰 발급
    @PostMapping("/coupon")
    public ResponseEntity issueCoupon(){

        CouponResponse couponResponse = CouponResponse.builder()
                .couponId(1)
                .discountAmount(23000)
                .isUsed(false)
                .build();


        return new ResponseEntity<>(new SingleResponseDto<>(couponResponse), HttpStatus.CREATED);
    }



    @Operation(summary = "쿠폰 조회", description = "쿠폰 조회을합니다.")
    //쿠폰 조회
    @GetMapping("/coupon/{userId}")
    public ResponseEntity listCoupon(@PathVariable int userId){

        CouponResponse couponResponse1 = CouponResponse.builder()
                .couponId(1)
                .discountAmount(23000)
                .isUsed(false)
                .build();
        CouponResponse couponResponse2 = CouponResponse.builder()
                .couponId(2)
                .discountAmount(32000)
                .isUsed(false)
                .build();

        List<CouponResponse> couponResponses = new ArrayList<>();
        couponResponses.add(couponResponse1);
        couponResponses.add(couponResponse2);


        return new ResponseEntity<>(new MultiResoponseDto<>(couponResponses), HttpStatus.CREATED);
    }
}
