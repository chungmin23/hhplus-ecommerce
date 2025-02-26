package kr.hhplus.be.server.infrastructure.coupon;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;
import java.util.Set;

@Repository
public class CouponCashRepository {

    private static final String COUPON_REQUEST_KEY = "coupon-%d-requests";
    private static final String COUPON_ISSUED_KEY = "coupon-%d-issued";

    private final RedisTemplate<String, String> redisTemplate;

    public CouponCashRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     *   쿠폰 발급 요청 추가 (ZADD)
     */
    public void addCouponRequest(Long couponId, Long userId) {
        String requestKey = String.format(COUPON_REQUEST_KEY, couponId);
        Double requestTime = (double) System.currentTimeMillis();
        redisTemplate.opsForZSet().add(requestKey, userId.toString(), requestTime);
    }

    /**
     *  특정 쿠폰 ID의 발급된 사용자 확인 (SISMEMBER)
     */
    public boolean isCouponAlreadyIssued(Long couponId, Long userId) {
        String issuedKey = String.format(COUPON_ISSUED_KEY, couponId);
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(issuedKey, userId.toString()));
    }

    /**
     * 선착순 쿠폰 발급 대상 조회 후 삭제 (ZRANGE + ZREM)
     */
    public Set<String> getTopCouponRequests(Long couponId, int count) {
        String requestKey = String.format(COUPON_REQUEST_KEY, couponId);
        Set<String> winners = new LinkedHashSet<>();

        for (int i = 0; i < count; i++) {
            Set<String> userIds = redisTemplate.opsForZSet().range(requestKey, 0, 0);
            if (userIds != null && !userIds.isEmpty()) {
                String userId = userIds.iterator().next();
                winners.add(userId);
                redisTemplate.opsForZSet().remove(requestKey, userId);
            }
        }

        return winners;
    }

    /**
     *  중복 발급 방지를 위해 발급된 사용자 저장 (SADD)
     */
    public void markCouponAsIssued(Long couponId, Set<String> issuedUsers) {
        String issuedKey = String.format(COUPON_ISSUED_KEY, couponId);
        redisTemplate.opsForSet().add(issuedKey, issuedUsers.toArray(new String[0]));
    }

    /**
     * 현재 Redis에 저장된 모든 쿠폰 요청 키 조회
     */
    public Set<String> getCouponRequestKeys() {
        return redisTemplate.keys(COUPON_REQUEST_KEY);
    }


}
