package kr.hhplus.be.server.domain.payment;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.order.OrderRepository;
import kr.hhplus.be.server.infrastructure.payment.PaymentRepository;
import kr.hhplus.be.server.infrastructure.user.UserRepository;
import org.springdoc.api.ErrorMessage;
import org.springframework.stereotype.Service;
import kr.hhplus.be.server.domain.common.ErroMessages;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository, UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Payment processPayment(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(ErroMessages.ORDER_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErroMessages.USER_NOT_FOUND));

        if (user.getBalance() < order.getTotalPrice()) {
            throw new IllegalArgumentException(ErroMessages.INSUFFICIENT_BALANCE);
        }

        user.setBalance(user.getBalance() - order.getTotalPrice());
        userRepository.save(user);

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotalPrice())
                .status(PaymentStatus.SUCCESS)
                .build();

        return paymentRepository.save(payment);
    }

}
