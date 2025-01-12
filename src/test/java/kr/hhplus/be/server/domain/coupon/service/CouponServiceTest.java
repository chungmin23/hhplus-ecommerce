package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.domain.common.ErroMessages;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.coupon.CouponIssueRepository;
import kr.hhplus.be.server.infrastructure.coupon.CouponRepository;
import kr.hhplus.be.server.infrastructure.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @InjectMocks
    private CouponService couponService;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponIssueRepository couponIssueRepository;

    @Mock
    private UserRepository userRepository; // Mock Repository

    private final Long VALID_COUPON_ID = 1L;
    private final Long VALID_USER_ID = 100L;

    // 쿠폰이 있는경우
    @DisplayName("쿠폰 있는지 확인")
    @Test
    void isCoupon_Success() {
        // Given
        Mockito.when(couponIssueRepository.findUnusedCouponByUserIdAndCouponId(VALID_USER_ID, VALID_COUPON_ID))
                .thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> couponService.userCoupon(VALID_COUPON_ID, VALID_USER_ID)
        );

        Assertions.assertEquals(ErroMessages.COUPON_NOT_FOUND, exception.getMessage());

    }
    
    //쿠폰 갯수를 초과를 한경우
    @DisplayName("쿠폰 갯수를 초과를 한경우")
    @Test
    void userCoupon_Success() {
        // Given
        Coupon coupon = Coupon.builder()
                .id(1l)
                .name("Discount Coupon")
                .maxIssueCount(5)
                .build();

        Mockito.when(couponRepository.findById(VALID_COUPON_ID))
                .thenReturn(Optional.of(coupon));

        Mockito.when(userRepository.findById(VALID_USER_ID))
                .thenReturn(Optional.of(new User()));

        Mockito.when(couponIssueRepository.countByCouponId(VALID_COUPON_ID))
                .thenReturn(5L); // 최대 발급 수량 도달

        // When & Then
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> couponService.issueCoupon(VALID_COUPON_ID, VALID_USER_ID)
        );

        Assertions.assertEquals(ErroMessages.GET_MAX_ISSUE_COUNT, exception.getMessage());

    }

    //
  

}