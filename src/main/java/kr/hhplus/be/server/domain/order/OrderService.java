package kr.hhplus.be.server.domain.order;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.order.OrderDetailRepository;
import kr.hhplus.be.server.interfaces.exception.ErroMessages;
import kr.hhplus.be.server.infrastructure.order.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {


    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public OrderService(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    //주문 정보 저장
    @Transactional
    public Order createOrder(User user,  Coupon coupon, int totalPrice, int discountPrice, List<OrderDetail> orderDetails) {
        // 최종 결제 금액 계산
        int finalPrice = totalPrice - discountPrice;
        if (finalPrice < 0) {
            throw new IllegalArgumentException("최종 결제 금액은 0원 이상이어야 합니다.");
        }

        // 주문 생성
        Order order = Order.builder()
                .user(user)
                .totalPrice(totalPrice)
                .discountPrice(discountPrice)
                .finalPrice(finalPrice)
                .status(OrderStatus.PENDING)
                .coupon(coupon)
                .build();

        order = orderRepository.save(order);

        for (OrderDetail orderDetail : orderDetails) {
            orderDetail.setOrder(order);
        }
        orderDetailRepository.saveAll(orderDetails);



        return order;
    }

    // 주문 id 확인
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(ErroMessages.ORDER_NOT_FOUND));
    }

    // 주문 상태 업데이트
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(ErroMessages.ORDER_NOT_FOUND));
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
