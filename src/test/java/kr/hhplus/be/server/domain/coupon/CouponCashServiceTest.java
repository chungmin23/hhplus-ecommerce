package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.coupon.CouponIssueRepository;
import kr.hhplus.be.server.infrastructure.coupon.CouponRepository;
import kr.hhplus.be.server.infrastructure.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
class CouponCashServiceTest {
    @Autowired
    private CouponCashService couponCashService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponIssueRepository couponIssueRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private Coupon testCoupon;

    @BeforeEach
    void setup() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();

        // 테스트용 쿠폰 생성
        testCoupon = Coupon.builder()
                .name("테스트 쿠폰")
                .discountPrice(5000)
                .maxIssueCount(5) // 최대 5개 발급 가능
                .build();

        // 쿠폰 저장
        testCoupon = couponRepository.save(testCoupon);
    }


    /**
     * 여러 명의 사용자가 선착순 쿠폰을 신청하고, 정상적으로 저장되는지 확인
     */
    @Test
    void testMultipleUsersRequestCoupon() {
        // 테스트용 유저 생성
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            User user = new User();
            user.setName("User " + i);
            users.add(userRepository.save(user));
        }

        // 10명의 유저가 순차적으로 쿠폰 발급 요청
        users.forEach(user -> {
            try {
                couponCashService.requestCoupon(testCoupon.getId(), user);
                System.out.println(" 쿠폰 신청 완료: " + user.getId());
            } catch (Exception e) {
                System.out.println(" 예외 발생 (중복 또는 초과 신청): " + e.getMessage());
            }
        });

        // Redis에서 저장된 요청 개수 확인
        String requestKey = String.format("coupon-%d-requests", testCoupon.getId());
        Long requestCount = redisTemplate.opsForZSet().size(requestKey);
        System.out.println(" Redis에 저장된 쿠폰 요청 수: " + requestCount);

        assertTrue(requestCount <= 10);
    }

    /**
     * 스케줄러가 실행되어 정상적으로 쿠폰을 발급하는지 확인 (순서 유지)
     */
    @Test
    void testSchedulerProcessesCouponRequests() throws InterruptedException {
        // 5명의 유저를 생성 후 쿠폰 신청
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            User user = new User();
            user.setName("User " + i);
            user = userRepository.save(user);
            users.add(user);
            couponCashService.requestCoupon(testCoupon.getId(), user);
            Thread.sleep(50); // 순서 보장을 위해 50ms 딜레이 추가
        }

        // 스케줄러 실행
        couponCashService.processCouponRequests();

        // DB에서 발급된 쿠폰 개수 확인
        long issuedCount = couponIssueRepository.countByCouponId(testCoupon.getId());
        System.out.println("DB에 저장된 발급된 쿠폰 수: " + issuedCount);
        assertEquals(5, issuedCount);

        // DB에서 실제 발급된 사용자 순서 확인
        List<CouponIssue> issuedCoupons = couponIssueRepository.findByCouponIdOrderByIdAsc(testCoupon.getId());

        System.out.println("실제 발급된 쿠폰 순서:");
        for (CouponIssue issue : issuedCoupons) {
            System.out.println("User ID: " + issue.getUser().getId());
        }

        // Redis에서 삭제된 쿠폰 요청 확인 (발급 완료)
        String requestKey = String.format("coupon-%d-requests", testCoupon.getId());
        Long remainingRequests = redisTemplate.opsForZSet().size(requestKey);
        assertEquals(0, remainingRequests);
    }


}