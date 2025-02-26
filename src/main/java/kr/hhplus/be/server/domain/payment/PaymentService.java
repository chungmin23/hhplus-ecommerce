package kr.hhplus.be.server.domain.payment;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.infrastructure.payment.PaymentRepository;
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
        Payment payment = Payment.builder()
                .order(order)
                .amount(amount)
                .status(PaymentStatus.SUCCESS)
                .build();
        return paymentRepository.save(payment);
    }

}
