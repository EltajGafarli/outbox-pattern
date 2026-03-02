package org.example.orderservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.example.orderservice.model.enums.OutboxStatus;

@Entity
@Table(
    name = "outbox_events",
    indexes = {
      @Index(name = "idx_outbox_status_available", columnList = "status, availableAt"),
      @Index(name = "idx_outbox_aggregate", columnList = "aggregateType, aggregateId")
    })
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OutboxEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false, length = 80)
  private String aggregateType;

  @Column(nullable = false, length = 80)
  private String aggregateId;

  @Column(nullable = false, length = 120)
  private String eventType;

  @Lob
  @Column(nullable = false, columnDefinition = "TEXT")
  private String payload;

  @Lob
  @Column(columnDefinition = "TEXT")
  private String headers;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private OutboxStatus status;

  @Column(nullable = false)
  private Integer retryCount;

  @Column(nullable = false)
  private OffsetDateTime occurredAt;

  @Column(nullable = false)
  private OffsetDateTime availableAt;

  private OffsetDateTime processedAt;

  @Column(length = 1000)
  private String errorMessage;

  @Column(nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @Column(nullable = false)
  private OffsetDateTime updatedAt;

  @PrePersist
  void prePersist() {
    OffsetDateTime now = OffsetDateTime.now();
    if (createdAt == null) {
      createdAt = now;
    }
    updatedAt = now;
    if (status == null) {
      status = OutboxStatus.PENDING;
    }
    if (retryCount == null) {
      retryCount = 0;
    }
    if (occurredAt == null) {
      occurredAt = now;
    }
    if (availableAt == null) {
      availableAt = now;
    }
  }

  @PreUpdate
  void preUpdate() {
    updatedAt = OffsetDateTime.now();
  }
}
