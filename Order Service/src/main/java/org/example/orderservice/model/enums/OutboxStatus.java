package org.example.orderservice.model.enums;

public enum OutboxStatus {
  PENDING,
  PROCESSING,
  PUBLISHED,
  FAILED,
  DEAD_LETTER
}
