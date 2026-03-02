package org.example.orderservice.producer;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {

  private final KafkaTemplate<String, String> kafkaTemplate;

  @Value("${app.kafka.topic.order-events:order-events}")
  private String orderEventsTopic;

  @Value("${app.kafka.producer.send-timeout-ms:5000}")
  private long sendTimeoutMs;

  public void send(String key, String payload) {
    try {
      kafkaTemplate.send(orderEventsTopic, key, payload).get(sendTimeoutMs, TimeUnit.MILLISECONDS);
    } catch (Exception exception) {
      throw new IllegalStateException("Kafka event publish failed", exception);
    }
  }
}
