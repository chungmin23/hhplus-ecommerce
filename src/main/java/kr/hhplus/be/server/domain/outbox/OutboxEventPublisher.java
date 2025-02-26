package kr.hhplus.be.server.domain.outbox;

import kr.hhplus.be.server.infrastructure.outbox.OutboxEventRepository;
import kr.hhplus.be.server.interfaces.api.kafka.KafkaProducer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxEventPublisher {
    private final OutboxEventRepository outboxEventRepository;
    private final KafkaProducer kafkaProducer;

    public OutboxEventPublisher(OutboxEventRepository outboxEventRepository, KafkaProducer kafkaProducer) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaProducer = kafkaProducer;
    }

    @Scheduled(fixedDelay = 5000)
    public void publishEvents() {
        List<OutboxEvent> events = outboxEventRepository.findByStatus(OutboxEvent.Status.INIT);
        for (OutboxEvent event : events) {
            try {
                kafkaProducer.send(event.getTopic(), event.getPayload());
                event.markAsPublished();
                outboxEventRepository.save(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}