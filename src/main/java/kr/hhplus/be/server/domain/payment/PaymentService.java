package kr.hhplus.be.server.domain.payment;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.infrastructure.payment.PaymentRepository;
import kr.hhplus.be.server.interfaces.exception.ErroMessages;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
 
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;

    }

    // 주문 성공 상태 업데이트
    @Transactional
    public Payment savePayment(Order order, int amount) {
        // 이미 결제된 주문인지 확인 (중복 결제 방지)
//        if (order.getStatus().equals(OrderStatus.PAID)) {
//            throw new IllegalArgumentException("이미 결제 완료된 주문입니다.");
//        }

        Payment payment = Payment.builder()
                .order(order)
                .amount(amount)
                .status(PaymentStatus.SUCCESS)
                .build();
        return paymentRepository.save(payment);
    }

}
