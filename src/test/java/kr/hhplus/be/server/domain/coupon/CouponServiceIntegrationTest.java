package kr.hhplus.be.server.domain.coupon;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.coupon.CouponIssueRepository;
import kr.hhplus.be.server.infrastructure.coupon.CouponRepository;
import kr.hhplus.be.server.infrastructure.user.UserRepository;
import kr.hhplus.be.server.interfaces.exception.ErroMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Transactional
class CouponServiceIntegrationTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponIssueRepository couponIssueRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Coupon testCoupon;

    @BeforeEach
    void setUp() {
        // 사용자 생성
        testUser = User.builder()
                .name("Test User")
                .balance(10000)
                .build();
        userRepository.save(testUser);

        // 쿠폰 생성
        testCoupon = Coupon.builder()
                .name("Discount Coupon")
                .discountPrice(1000)
                .maxIssueCount(5)
                .build();
        couponRepository.save(testCoupon);
    }

    @DisplayName("쿠폰 발급, 조회, 사용, 검증 통합 테스트")
    @Test
    void couponFlowIntegrationTest() {
        // 쿠폰 발급
        CouponIssue issuedCoupon = couponService.issueCoupon(testCoupon.getId(), testUser.getId());
        Assertions.assertNotNull(issuedCoupon);
        Assertions.assertEquals(testCoupon.getId(), issuedCoupon.getCoupon().getId());
        Assertions.assertEquals(testUser.getId(), issuedCoupon.getUser().getId());

        // 사용 가능한 쿠폰 조회
        List<CouponIssue> userCoupons = couponService.getUserCoupons(testUser.getId());
        Assertions.assertNotNull(userCoupons);
        Assertions.assertEquals(1, userCoupons.size());
        Assertions.assertFalse(userCoupons.get(0).isStatus()); // 쿠폰 미사용 여부 확인

        // 쿠폰 검증
        int totalPrice = 5000;
        Coupon validCoupon = couponService.validateCouponForOrder(testCoupon.getId(), testUser.getId(), totalPrice);
        Assertions.assertNotNull(validCoupon);
        Assertions.assertEquals(testCoupon.getId(), validCoupon.getId());

        // 쿠폰 사용
        couponService.userCoupon(testCoupon.getId(), testUser.getId());
        CouponIssue updatedCoupon = couponIssueRepository.findById(issuedCoupon.getId())
                .orElseThrow(() -> new IllegalArgumentException(ErroMessages.COUPON_NOT_FOUND));
        Assertions.assertTrue(updatedCoupon.isStatus()); // 쿠폰 사용 여부 확인
    }

}

