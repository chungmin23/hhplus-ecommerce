package kr.hhplus.be.server.interfaces.api.coupon.schedullar;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.coupon.CouponCashService;
import kr.hhplus.be.server.infrastructure.coupon.CouponRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class CouponScheduler {

    private final CouponCashService couponCashService;
    private final CouponRepository couponRepository;


    public CouponScheduler(CouponCashService couponCashService, CouponRepository couponRepository) {
        this.couponCashService = couponCashService;
        this.couponRepository = couponRepository;
    }

    /**
     *  스케줄러  (매 10초 실행)
     */
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void processCouponRequests() {
        Set<String> requestKeys = couponCashService.getRedisCouponKeys();
        if (requestKeys == null || requestKeys.isEmpty()) return;

        for (String key : requestKeys) {
            Long couponId = extractCouponId(key);
            couponRepository.findById(couponId).ifPresent(couponCashService::issueCoupons);
        }
    }

    private Long extractCouponId(String key) {
        return Long.parseLong(key.split("-")[1]);
    }

}
