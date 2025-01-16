package kr.hhplus.be.server.domain.order;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.interfaces.exception.ErroMessages;
import kr.hhplus.be.server.infrastructure.order.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {


    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    //주문 정보 저장
    @Transactional
    public Order createOrder(User user,  Coupon coupon, int totalPrice, int discountPrice) {
        // Create Order
        Order order = Order.builder()
                .user(user)
                .totalPrice(totalPrice)
                .discountPrice(discountPrice)
                .status(OrderStatus.PENDING)
                .coupon(coupon)
                .build();

        return orderRepository.save(order);
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
