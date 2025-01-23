package kr.hhplus.be.server.domain.coupon;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.infrastructure.coupon.CouponRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@Transactional
public class CouponLockTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;


    @BeforeEach
    void setUp(){
        Coupon coupon = Coupon.builder()
                .maxIssueCount(10)
                .name("Discount Coupon")
                .discountPrice(1000)
                .build();
    }

    @DisplayName("쿠폰 발급")
    @Test
    void testConcurrentCouponIssue() throws InterruptedException {
        // Given
        Long couponId = couponRepository.findAll().get(0).getId();
        Long userId = 1L;

        // When
        int threadCount = 5; // 동시 실행할 스레드 수
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {

            Long threadUserId = userId+i;
            executorService.submit(() -> {
                try {
                    couponService.issueCoupon(couponId,threadUserId);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 작업을 완료할 때까지 대기

        // Then
        Coupon coupon = couponRepository.findById(couponId).orElseThrow();
        System.out.println("남은 수량: " + coupon.getMaxIssueCount());
        assertEquals(10 - threadCount, coupon.getMaxIssueCount()); // 남은 수량 확인
    }

}
