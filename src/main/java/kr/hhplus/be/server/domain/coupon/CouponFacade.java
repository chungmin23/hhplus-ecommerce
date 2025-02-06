package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CouponFacade {

    private final CouponService couponService;
    private final UserService userService;

    private final CouponCashService couponCashService;


    public CouponFacade(CouponService couponService, UserService userService, CouponCashService couponCashService) {
        this.couponService = couponService;
        this.userService = userService;
        this.couponCashService = couponCashService;
    }

    /**
     * 쿠폰 발급
     */
    @Transactional
    public Boolean issueCoupon(Long couponId, Long userId) {
        // 사용자 검증
        User user = userService.getUserById(userId);

        // 쿠폰 발급
        return couponCashService.requestCoupon(couponId, user);
    }

    /**
     * 쿠폰 사용
     */
    @Transactional
    public void useCoupon(Long couponId, Long userId) {
        // 사용자 검증
        userService.getUserById(userId);

        // 쿠폰 사용
        couponService.userCoupon(couponId, userId);
    }

    /**
     * 쿠폰 검증 및 할인 금액 계산
     */
    @Transactional
    public Coupon validateCouponForOrder(Long couponId, Long userId, int totalPrice) {
        // 사용자 검증
        userService.getUserById(userId);

        // 2쿠폰 검증
        return couponService.validateCouponForOrder(couponId, userId, totalPrice);
    }


    /**
     * 사용자의 미사용 쿠폰 조회
     */
    public List<CouponIssue> getUserCoupons(Long userId) {
        userService.getUserById(userId);

        return couponService.getUserCoupons(userId);
    }
}
