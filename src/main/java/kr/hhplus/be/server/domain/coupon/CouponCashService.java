package kr.hhplus.be.server.domain.coupon;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.coupon.CouponCashRepository;
import kr.hhplus.be.server.infrastructure.coupon.CouponIssueRepository;
import kr.hhplus.be.server.infrastructure.coupon.CouponRepository;
import kr.hhplus.be.server.infrastructure.user.UserRepository;
import kr.hhplus.be.server.interfaces.exception.ErroMessages;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CouponCashService {

    private final CouponRepository couponRepository;

    private final CouponIssueRepository couponIssueRepository;

    private final UserRepository userRepository;

    private final CouponCashRepository couponCashRepository;


    public CouponCashService(CouponRepository couponRepository, CouponIssueRepository couponIssueRepository, UserRepository userRepository, CouponCashRepository couponCashRepository) {
        this.couponRepository = couponRepository;
        this.couponIssueRepository = couponIssueRepository;
        this.userRepository = userRepository;
        this.couponCashRepository = couponCashRepository;
    }




    /**
     *  쿠폰 발급 처리
     */
    public void issueCoupons(Coupon coupon) {
        // DB에서 잔여 수량 확인
        int availableCount = coupon.getMaxIssueCount() - (int) couponIssueRepository.countByCouponId(coupon.getId());
        if (availableCount <= 0) return;

        // Redis에서 선착순 유저 가져오기
        Set<String> winners = couponCashRepository.getTopCouponRequests(coupon.getId(), availableCount);
        if (winners.isEmpty()) return;

        // 쿠폰 발급 내역 DB 저장
        List<CouponIssue> couponIssues = winners.stream()
                .map(userId -> {
                    User user = userRepository.findById(Long.parseLong(userId))
                            .orElseThrow(() -> new IllegalArgumentException(ErroMessages.USER_NOT_FOUND));
                    return new CouponIssue(coupon, user);
                })
                .collect(Collectors.toList());
        couponIssueRepository.saveAll(couponIssues);

        // Redis에서 중복 발급 방지 처리
        couponCashRepository.markCouponAsIssued(coupon.getId(), winners);


    }


    // 쿠폰 발급 요청 등록
    public boolean requestCoupon(Long couponId, User user){
        // 중복 발급 여부 확인
        if (couponCashRepository.isCouponAlreadyIssued(couponId, user.getId())) {
            throw new IllegalArgumentException(ErroMessages.COUPON_ALREADY_ISSUED);
        }

        // 쿠폰 요청 등록
        couponCashRepository.addCouponRequest(couponId, user.getId());
        return true;
    }

    public Set<String> getRedisCouponKeys() {
        return couponCashRepository.getCouponRequestKeys();
    }


}
