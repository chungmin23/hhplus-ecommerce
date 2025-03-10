package kr.hhplus.be.server.interfaces.api.coupon.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.hhplus.be.server.domain.coupon.CouponFacade;
import kr.hhplus.be.server.domain.coupon.CouponIssue;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.interfaces.api.common.MultiResoponseDto;
import kr.hhplus.be.server.interfaces.api.common.SingleResponseDto;
import kr.hhplus.be.server.interfaces.api.coupon.dto.CouponRequest;
import kr.hhplus.be.server.interfaces.api.coupon.dto.CouponResponse;
import kr.hhplus.be.server.interfaces.api.payment.controller.PaymentController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupon")
public class CouponController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    @Autowired
    private CouponFacade couponFacade;

    @Operation(summary = "쿠폰 발급", description = "쿠폰 발급을합니다.")
    //쿠폰 발급
    @PostMapping("/issue")
    public ResponseEntity issueCoupon(@RequestBody CouponRequest couponRequest){
        logger.info("Received request:  body={}",  couponRequest);

        Boolean success = couponFacade.issueCoupon(couponRequest.getCouponId(), couponRequest.getUserId());

      
        return new ResponseEntity<>(new SingleResponseDto<>("쿠폰 발급 요청이 되었습니다"), HttpStatus.CREATED);
    }



    @Operation(summary = "쿠폰 조회", description = "쿠폰 조회을 합니다.")
    //쿠폰 조회
    @GetMapping("/{userId}")
    public ResponseEntity listCoupon(@PathVariable Long userId){
        logger.info("Received request:  param1={}",  userId);

        List<CouponIssue> couponIssues = couponFacade.getUserCoupons(userId);


        logger.info("Sending response: {}", couponIssues);
        return new ResponseEntity<>(new MultiResoponseDto<>(couponIssues), HttpStatus.OK);
    }
}
