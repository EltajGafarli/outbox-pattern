package org.example.orderservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.orderservice.service.OutboxPublishService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderOutboxEventProducerScheduler {

  private final OutboxPublishService outboxPublishService;

  @Value("${outbox.producer.loop.batch-size:20}")
  private int batchSize;

  @Scheduled(
      fixedDelayString = "${outbox.producer.loop.delay-ms:60000}",
      initialDelayString = "1000")
  public void publishPendingEvents() {
    int publishedCount = outboxPublishService.publishPendingOrderEvents(batchSize);
    if (publishedCount > 0) {
      log.info("Published {} outbox event(s)", publishedCount);
    }
  }
}
