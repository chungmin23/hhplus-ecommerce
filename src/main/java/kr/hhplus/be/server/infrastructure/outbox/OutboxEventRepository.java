package kr.hhplus.be.server.infrastructure.outbox;

import kr.hhplus.be.server.domain.outbox.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long>
{
    List<OutboxEvent> findByStatus(OutboxEvent.Status status);

    Optional<OutboxEvent> findTopByPayloadContaining(String payloadPart);
}
