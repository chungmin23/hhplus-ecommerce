package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.outbox.OutboxEvent;
import kr.hhplus.be.server.infrastructure.outbox.OutboxEventRepository;
import kr.hhplus.be.server.interfaces.api.kafka.KafkaProducer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PaymentEventListener {
    private final OutboxEventRepository outboxEventRepository;
    private final KafkaProducer kafkaProducer;

    public PaymentEventListener(OutboxEventRepository outboxEventRepository, KafkaProducer kafkaProducer) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaProducer = kafkaProducer;
    }

    // 1) Outbox 테이블에 기록
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void saveOutbox(PaymentSuccessEvent event) {
        OutboxEvent outboxEvent = OutboxEvent.create(
                "order-paid-events",
                "ORDER_PAID",
                String.format("{\"orderId\":%d}", event.getOrderKey())
        );
        outboxEventRepository.save(outboxEvent);
    }

    // 2) 메시지 발행
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendOrderInfo(PaymentSuccessEvent event) {
        OutboxEvent outboxEvent = outboxEventRepository.findTopByPayloadContaining(String.format("\"orderId\":%d", event.getOrderKey()))
                .orElseThrow(() -> new IllegalStateException("Outbox 이벤트가 존재하지 않습니다."));

        String message = outboxEvent.getPayload();
        kafkaProducer.send(outboxEvent.getTopic(), message);

        outboxEvent.markAsPublished();
        outboxEventRepository.save(outboxEvent);
    }
}