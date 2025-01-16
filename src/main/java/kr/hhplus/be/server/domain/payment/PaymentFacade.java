package kr.hhplus.be.server.domain.payment;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.user.UserService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class PaymentFacade {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final UserService userService;

    public PaymentFacade(PaymentService paymentService, OrderService orderService, UserService userService) {
        this.paymentService = paymentService;
        this.orderService = orderService;
        this.userService = userService;
    }

    @Transactional
    public Payment processPayment(Long orderId, Long userId) {
        // 주문 조회
        Order order = orderService.getOrderById(orderId);

        // 사용자 잔액 차감
        userService.deductBalance(userId, order.getTotalPrice());

        // 주문 상태 업데이트
        orderService.updateOrderStatus(orderId, OrderStatus.PAID);

        // 결제 저장
        return paymentService.savePayment(order, order.getTotalPrice());
    }
}
