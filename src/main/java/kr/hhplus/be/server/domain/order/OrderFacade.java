package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderFacade {
    private final OrderService orderService;
    private final ProductService productService;
    private final UserService userService;
    private final CouponService couponService;

    public OrderFacade(OrderService orderService, ProductService productService, UserService userService, CouponService couponService) {
        this.orderService = orderService;
        this.productService = productService;
        this.userService = userService;
        this.couponService = couponService;
    }


    public Order createOrder(Long userId, List<OrderDetailRequest> orderDetails, Long couponId) {
        // 유저 검증
        User user = userService.getUserById(userId);

        
        //재고확인 및 재고 감소
        int totalPrice = 0;
        for (OrderDetailRequest detailRequest : orderDetails) {

            productService.validateAndReduceStock(detailRequest.getProductId(), detailRequest.getQuantity());
            
            Product product = productService.getProductById(detailRequest.getProductId());
            totalPrice += product.getPrice() * detailRequest.getQuantity();
        }


        // 쿠폰 사용
        Coupon coupon = null;
        int discountPrice = 0;
        if (couponId != null) {
            coupon = couponService.validateCouponForOrder(couponId, userId, totalPrice);
            discountPrice = coupon.getDiscountPrice();
        }

        // 주문 생성
        return orderService.createOrder(user, coupon, totalPrice, discountPrice);
    }
}
