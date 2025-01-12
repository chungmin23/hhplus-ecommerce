package kr.hhplus.be.server.interfaces.api.coupon.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponIssue;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import kr.hhplus.be.server.interfaces.api.common.MultiResoponseDto;
import kr.hhplus.be.server.interfaces.api.common.SingleResponseDto;
import kr.hhplus.be.server.interfaces.api.coupon.dto.CouponRequest;
import kr.hhplus.be.server.interfaces.api.coupon.dto.CouponResponse;
import kr.hhplus.be.server.interfaces.api.user.dto.BalanceChargeRequset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/coupon")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @Operation(summary = "쿠폰 발급", description = "쿠폰 발급을합니다.")
    //쿠폰 발급
    @PostMapping("/issue")
    public ResponseEntity issueCoupon(@RequestBody CouponRequest couponRequest){
        CouponIssue issueCoupon = couponService.issueCoupon(couponRequest.getCouponId(), couponRequest.getUserId());
        return new ResponseEntity<>(new SingleResponseDto<>(issueCoupon), HttpStatus.CREATED);
    }



    @Operation(summary = "쿠폰 조회", description = "쿠폰 조회을 합니다.")
    //쿠폰 조회
    @GetMapping("/{userId}")
    public ResponseEntity listCoupon(@PathVariable Long userId){

        List<CouponIssue> couponIssues = couponService.getUserCoupons(userId);

        return new ResponseEntity<>(new MultiResoponseDto<>(couponIssues), HttpStatus.OK);
    }
}
