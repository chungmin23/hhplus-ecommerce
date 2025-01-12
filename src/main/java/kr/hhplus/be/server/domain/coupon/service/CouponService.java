package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.domain.common.ErroMessages;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponIssue;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.coupon.CouponIssueRepository;
import kr.hhplus.be.server.infrastructure.coupon.CouponRepository;
import kr.hhplus.be.server.infrastructure.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponService {
    private final CouponRepository couponRepository;

    private final CouponIssueRepository couponIssueRepository;

    private final UserRepository userRepository;

    public CouponService(CouponRepository couponRepository, CouponIssueRepository couponIssueRepository, UserRepository userRepository) {
        this.couponRepository = couponRepository;
        this.couponIssueRepository = couponIssueRepository;
        this.userRepository = userRepository;
    }

    //쿠폰 발급
    public CouponIssue issueCoupon(Long couponId, Long userId){
        // 쿠폰 있는지 확인
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(()-> new IllegalArgumentException(ErroMessages.COUPON_NOT_FOUND));

        //사용자 있는지 확인
        User user = userRepository.findById(userId).orElseThrow(()-> new IllegalArgumentException(ErroMessages.USER_NOT_FOUND));

        //쿠폰 갯수 확인
        long issuedCount = couponIssueRepository.countByCouponId(couponId);

        if(issuedCount >= coupon.getMaxIssueCount()){
            throw new IllegalArgumentException(ErroMessages.GET_MAX_ISSUE_COUNT);
        }

        CouponIssue couponIssue = CouponIssue.builder()
                .coupon(coupon)
                .user(user)
                .status(false)
                .build();

        return couponIssueRepository.save(couponIssue);
    }

    // 쿠폰 사용
    public void userCoupon(Long couponId, Long userId){
        CouponIssue couponIssue = couponIssueRepository.findUnusedCouponByUserIdAndCouponId(userId, couponId)
                .orElseThrow(() -> new IllegalArgumentException(ErroMessages.COUPON_NOT_FOUND));

        couponIssue.setStatus(true);
        couponIssueRepository.save(couponIssue);
    }

    //사용자의 쿠폰 조회
    public List<CouponIssue> getUserCoupons(long userId){
        return couponIssueRepository.findUnusedCouponByUserId(userId);
    }


}
