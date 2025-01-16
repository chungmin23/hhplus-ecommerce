package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.CouponIssue;
import kr.hhplus.be.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponIssueRepository extends JpaRepository<CouponIssue, Long> {
    //특정 사용자가 사용하지 않은 쿠폰 조회
    @Query("SELECT ci FROM CouponIssue ci WHERE ci.user.id = :userId and ci.status = false")
    List<CouponIssue> findUnusedCouponByUserId(@Param("userId") Long userId);

    // 특정 쿠폰 id 와 사용자 id로 사용되지 않은 쿠폰 조회
    @Query("SELECT ci FROM CouponIssue ci WHERE ci.coupon.id = :couponId AND ci.status = false")
    Optional<CouponIssue> findUnusedCouponByUserIdAndCouponId(@Param("userId") Long userId, @Param("couponId") Long couponId);

    // 쿠폰 ID별 발급된 쿠폰 수를 반환
    long countByCouponId(Long couponId);

}
