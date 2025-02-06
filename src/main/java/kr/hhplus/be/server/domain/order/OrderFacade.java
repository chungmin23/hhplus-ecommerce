package kr.hhplus.be.server.domain.order;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponFacade;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.interfaces.api.order.dto.OrderDetailRequestInfo;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderFacade {
    private final OrderService orderService;
    private final ProductService productService;

    private final CouponFacade couponFacade;
    private final UserService userService;
    private final CouponService couponService;

    public OrderFacade(OrderService orderService, ProductService productService, CouponFacade couponFacade, UserService userService, CouponService couponService) {
        this.orderService = orderService;
        this.productService = productService;
        this.couponFacade = couponFacade;
        this.userService = userService;
        this.couponService = couponService;
    }

    @Transactional
    public Order createOrder(OrderDetailRequestInfo OrderDetailRequestInfo) {
        // 유저 검증
        User user = userService.getUserById(OrderDetailRequestInfo.getUserId());


        
        //주문할 상품 재고 확인 및 감소
        int totalPrice = 0;
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (OrderDetailRequest detailRequest : OrderDetailRequestInfo.getOrderDetailRequests()) {
            //재고 확인 & 감소 
            productService.validateAndReduceStock(detailRequest.getProductId(), detailRequest.getQuantity());
            //최신 가격 조회
            Product product = productService.getProductById(detailRequest.getProductId());
            totalPrice += product.getPrice() * detailRequest.getQuantity();

            // 주문 상세 생성
            OrderDetail orderDetail = OrderDetail.builder()
                    .product(product)
                    .quantity(detailRequest.getQuantity())
                    .price(product.getPrice() * detailRequest.getQuantity())
                    .build();

            orderDetails.add(orderDetail);
            totalPrice += orderDetail.getPrice();

        }


        // 쿠폰 사용
        Coupon coupon = null;
        int discountPrice = 0;
        if (OrderDetailRequestInfo.getCouponId() != null) {
            coupon = couponFacade.validateCouponForOrder(OrderDetailRequestInfo.getCouponId(), OrderDetailRequestInfo.getUserId(), totalPrice);
            discountPrice = coupon.getDiscountPrice();
        }


        // 주문 생성
        return orderService.createOrder(user, coupon, totalPrice, discountPrice,orderDetails);
    }
}
