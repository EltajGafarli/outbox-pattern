package org.example.orderservice.repository;

import java.time.OffsetDateTime;
import java.util.List;
import org.example.orderservice.model.entity.OutboxEntity;
import org.example.orderservice.model.enums.OutboxStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxRepository extends JpaRepository<OutboxEntity, Long> {

  List<OutboxEntity> findByStatusAndAvailableAtLessThanEqualOrderByOccurredAtAsc(
      OutboxStatus status, OffsetDateTime availableAt, Pageable pageable);

  List<OutboxEntity> findAllByAggregateTypeAndAggregateIdOrderByOccurredAtAsc(
      String aggregateType, String aggregateId);

  List<OutboxEntity> findAllByStatusAndEventTypeAndAvailableAtLessThanEqualOrderByOccurredAtAsc(
      OutboxStatus status, String eventType, OffsetDateTime availableAt, Pageable pageable);
}
