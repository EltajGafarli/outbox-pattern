package org.example.orderservice.service.impl;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.orderservice.model.entity.OutboxEntity;
import org.example.orderservice.model.enums.OutboxStatus;
import org.example.orderservice.producer.OrderEventProducer;
import org.example.orderservice.repository.OutboxRepository;
import org.example.orderservice.service.OutboxPublishService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutboxPublishServiceImpl implements OutboxPublishService {

  private final OutboxRepository outboxRepository;
  private final OrderEventProducer orderEventProducer;

  @Value("${outbox.producer.loop.max-retry-count:5}")
  private int maxRetryCount;

  @Value("${outbox.producer.loop.retry-delay-seconds:15}")
  private long retryDelaySeconds;

  @Override
  @Transactional
  public int publishPendingEvents(int batchSize) {
    List<OutboxEntity> events =
        outboxRepository.findByStatusAndAvailableAtLessThanEqualOrderByOccurredAtAsc(
            OutboxStatus.PENDING, OffsetDateTime.now(), PageRequest.of(0, Math.max(1, batchSize)));

    int publishedCount = 0;
    for (OutboxEntity event : events) {
      try {
        orderEventProducer.send(event.getAggregateId(), event.getPayload());
        event.setStatus(OutboxStatus.PUBLISHED);
        event.setProcessedAt(OffsetDateTime.now());
        event.setErrorMessage(null);
        publishedCount++;
      } catch (Exception exception) {
        markFailure(event, exception);
      }
    }

    return publishedCount;
  }

  @Override
  @Transactional
  public int publishPendingOrderEvents(int batchSize) {
    List<OutboxEntity> events =
        outboxRepository.findAllByStatusAndEventTypeAndAvailableAtLessThanEqualOrderByOccurredAtAsc(
            OutboxStatus.PENDING, "ORDER_PLACED", OffsetDateTime.now(),
            PageRequest.of(0, Math.max(1, batchSize))
        );

    int publishedCount = 0;

    for (OutboxEntity event : events) {
      try {
        orderEventProducer.send(event.getAggregateId(), event.getPayload());
        event.setStatus(OutboxStatus.PUBLISHED);
        event.setProcessedAt(OffsetDateTime.now());
        event.setErrorMessage(null);
        publishedCount++;
      } catch (Exception exception) {
        markFailure(event, exception);
      }
    }

    return publishedCount;

  }

  private void markFailure(OutboxEntity event, Exception exception) {
    int nextRetryCount = (event.getRetryCount() == null ? 0 : event.getRetryCount()) + 1;
    event.setRetryCount(nextRetryCount);
    event.setProcessedAt(OffsetDateTime.now());
    event.setErrorMessage(truncate(exception.getMessage()));

    if (nextRetryCount >= maxRetryCount) {
      event.setStatus(OutboxStatus.DEAD_LETTER);
      return;
    }

    event.setStatus(OutboxStatus.PENDING);
    event.setAvailableAt(OffsetDateTime.now().plusSeconds(retryDelaySeconds));
  }

  private String truncate(String value) {
    if (value == null) {
      return "unknown error";
    }
    return value.length() > 1000 ? value.substring(0, 1000) : value;
  }
}
