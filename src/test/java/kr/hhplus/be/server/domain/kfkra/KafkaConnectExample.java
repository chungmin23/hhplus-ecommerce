package kr.hhplus.be.server.domain.kfkra;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.kafka.test.context.EmbeddedKafka;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"test-topic"})
class KafkaConnectExample {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();

    @KafkaListener(topics = "test-topic", groupId = "test-group")
    void listen(String message) {
        messageQueue.offer(message);
    }

    @Test
    void testSendAndReceive() throws InterruptedException {
        kafkaTemplate.send("test-topic", "hello kafka");

        // 최대 3초 기다리면서 메시지 수신 확인
        String receivedMessage = messageQueue.poll(3, TimeUnit.SECONDS);

        assertThat(receivedMessage).isEqualTo("hello kafka");
    }
}