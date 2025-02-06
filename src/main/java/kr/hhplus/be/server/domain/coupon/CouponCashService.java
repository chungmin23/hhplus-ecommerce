package kr.hhplus.be.server.domain.coupon;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.user.User;
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

    private static final String COUPON_REQUEST_KEY = "coupon-%d-requests";
    private static final String COUPON_ISSUED_KEY = "coupon-%d-issued";

    private final RedisTemplate<String, String> redisTemplate;

    private final CouponRepository couponRepository;

    private final CouponIssueRepository couponIssueRepository;

    private final UserRepository userRepository;


    public CouponCashService(RedisTemplate<String, String> redisTemplate, CouponRepository couponRepository, CouponIssueRepository couponIssueRepository, UserRepository userRepository) {
        this.redisTemplate = redisTemplate;
        this.couponRepository = couponRepository;
        this.couponIssueRepository = couponIssueRepository;
        this.userRepository = userRepository;
    }

    /**
     *  스케줄러  (매 10초 실행)
     */
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void processCouponRequests() {
        Set<String> requestKeys = redisTemplate.keys("coupon-*-requests");
        if (requestKeys == null || requestKeys.isEmpty()) return;

        for (String key : requestKeys) {
            Long couponId = extractCouponId(key);
           couponRepository.findById(couponId).ifPresent(this::issueCoupons);
        }
    }

    /**
     *  쿠폰 발급 처리
     */
    private void issueCoupons(Coupon coupon) {
        String requestKey = String.format(COUPON_REQUEST_KEY, coupon.getId());
        String issuedKey = String.format(COUPON_ISSUED_KEY, coupon.getId());

        // DB에서 잔여 수량 확인
        int availableCount = coupon.getMaxIssueCount() - (int) couponIssueRepository.countByCouponId(coupon.getId());
        if (availableCount <= 0) return;

        // Redis에서  유저 조회
        Set<String> getRequestCoupons = getTopCouponRequests(requestKey, availableCount);
        if (getRequestCoupons == null || getRequestCoupons.isEmpty()) return;

        // 쿠폰 발급 내역 DB 저장
        List<CouponIssue> couponIssues = getRequestCoupons.stream()
                .map(userId -> {
                    User user = userRepository.findById(Long.parseLong(userId))
                            .orElseThrow(() -> new IllegalArgumentException(ErroMessages.USER_NOT_FOUND));
                    return new CouponIssue(coupon, user);
                })
                .collect(Collectors.toList());
        couponIssueRepository.saveAll(couponIssues);

        // Redis에서 중복 발급 방지 처리
        redisTemplate.opsForSet().add(issuedKey, getRequestCoupons.toArray(new String[0]));


    }

    /**
     *  Redis 키에서 쿠폰 ID 추출
     */
    private Long extractCouponId(String key) {
        return Long.parseLong(key.split("-")[1]);
    }


    private Set<String> getTopCouponRequests(String requestKey, int count) {
        Set<String> getRequestCoupon = new LinkedHashSet<>(); // 순서 유지

        for (int i = 0; i < count; i++) {
            Set<String> userIds = redisTemplate.opsForZSet().range(requestKey, 0, 0);
            if (userIds != null && !userIds.isEmpty()) {
                String userId = userIds.iterator().next();
                getRequestCoupon.add(userId);
                redisTemplate.opsForZSet().remove(requestKey, userId);
            }
        }

        return getRequestCoupon;
    }

    // 쿠폰 발급 요청 등록
    public boolean requestCoupon(Long couponId, User user){
        String requestKey = String.format(COUPON_REQUEST_KEY, couponId);
        String issuedKey = String.format(COUPON_ISSUED_KEY, couponId);


        // 중복 발급 여부 확인
        Boolean alreadyIssued = redisTemplate.opsForSet().isMember(issuedKey, user.getId().toString());
        if (Boolean.TRUE.equals(alreadyIssued)) {
            throw new IllegalArgumentException(ErroMessages.COUPON_ALREADY_ISSUED);
        }

        // 요청 추가
        Double requestTime = (double) System.currentTimeMillis();
        redisTemplate.opsForZSet().add(requestKey, user.getId().toString(), requestTime);



        return true;
    }


}
