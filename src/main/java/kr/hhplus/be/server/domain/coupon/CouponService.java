package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.interfaces.exception.ErroMessages;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.coupon.CouponIssueRepository;
import kr.hhplus.be.server.infrastructure.coupon.CouponRepository;
import kr.hhplus.be.server.infrastructure.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponService {
    private final CouponRepository couponRepository;

    private final CouponIssueRepository couponIssueRepository;



    public CouponService(CouponRepository couponRepository, CouponIssueRepository couponIssueRepository) {
        this.couponRepository = couponRepository;
        this.couponIssueRepository = couponIssueRepository;
    }

    //쿠폰 발급
    public CouponIssue issueCoupon(Long couponId, User user, String name){
        // 쿠폰 있는지 확인
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(()-> new IllegalArgumentException(ErroMessages.COUPON_NOT_FOUND));


        // 동일한 쿠폰이 이미 발급되었는지 확인
        boolean alreadyIssued = couponIssueRepository.existsByUserIdAndCouponId(user.getId(), couponId);
        if (alreadyIssued) {
            throw new IllegalArgumentException(ErroMessages.COUPON_ALREADY_ISSUED);
        }


        //쿠폰 갯수 확인
        long issuedCount = couponIssueRepository.countByCouponId(couponId);

        if(issuedCount >= coupon.getMaxIssueCount()){
            throw new IllegalArgumentException(ErroMessages.GET_MAX_ISSUE_COUNT);
        }

        CouponIssue couponIssue = CouponIssue.builder()
                .coupon(coupon)
                .user(user)
                .name(name)
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
        return couponIssueRepository.findAllByUserIdAndStatus(userId,false);
    }

    //쿠폰 검증 및 할인 금액 계산
    public Coupon validateCouponForOrder(Long couponId, Long userId, int totalPrice) {

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException(ErroMessages.COUPON_NOT_FOUND));

        //사용 가능 쿠폰 인지 확인
        CouponIssue couponIssue = couponIssueRepository.findUnusedCouponByUserIdAndCouponId(userId, couponId)
                .orElseThrow(() -> new IllegalArgumentException(ErroMessages.COUPON_NOT_FOUND));

        //할인 금액 검증
        if (totalPrice < coupon.getDiscountPrice()) {
            throw new IllegalArgumentException(ErroMessages.COUPON_NOT_FOUND);
        }

        // 쿠폰 사용 완료
        couponAsUsed(couponId, userId);

        return coupon;
    }

    //쿠폰 사용 완료
    @Transactional
    public void couponAsUsed(Long couponId, Long userId) {
        CouponIssue couponIssue = couponIssueRepository.findUnusedCouponByUserIdAndCouponId(userId, couponId)
                .orElseThrow(() -> new IllegalArgumentException(ErroMessages.COUPON_NOT_FOUND));

        couponIssue.setStatus(true);
        couponIssue.setUsedAt(LocalDateTime.now());
        couponIssueRepository.save(couponIssue);
    }




}
