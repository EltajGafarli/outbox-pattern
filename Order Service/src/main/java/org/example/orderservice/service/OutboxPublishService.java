package org.example.orderservice.service;

public interface OutboxPublishService {

  int publishPendingEvents(int batchSize);

  int publishPendingOrderEvents(int batchSize);
}
