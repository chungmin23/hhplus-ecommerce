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

       // 유저 검증 (해당 주문의 userId와 요청 userId 비교)
        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("해당 주문의 결제 권한이 없습니다.");
        }

        // 사용자 잔액 차감
        userService.deductBalance(userId, order.getFinalPrice());

        // 주문 상태 업데이트
        orderService.updateOrderStatus(orderId, OrderStatus.PAID);

        // 결제 저장
        return paymentService.savePayment(order, order.getFinalPrice());
    }
}
