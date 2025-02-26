package kr.hhplus.be.server.domain.outbox;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;
    private String eventType;
    private String payload;

    @Enumerated(EnumType.STRING)
    private Status status;

    private OutboxEvent(String topic, String eventType, String payload, Status status) {
        this.topic = topic;
        this.eventType = eventType;
        this.payload = payload;
        this.status = status;
    }

    public static OutboxEvent create(String topic, String eventType, String payload) {
        return new OutboxEvent(topic, eventType, payload, Status.INIT);
    }

    public void markAsPublished() {
        this.status = Status.PUBLISHED;
    }

    public enum Status {
        INIT, PUBLISHED, FAILED
    }

}