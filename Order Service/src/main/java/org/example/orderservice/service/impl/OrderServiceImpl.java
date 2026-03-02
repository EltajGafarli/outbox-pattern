package org.example.orderservice.service.impl;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.orderservice.mapper.OrderMapper;
import org.example.orderservice.model.business.Order;
import org.example.orderservice.model.entity.OrderEntity;
import org.example.orderservice.model.entity.OutboxEntity;
import org.example.orderservice.model.enums.OutboxStatus;
import org.example.orderservice.repository.OrderRepository;
import org.example.orderservice.repository.OutboxRepository;
import org.example.orderservice.service.OrderService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private static final String ORDER_AGGREGATE_TYPE = "ORDER";
  private static final String ORDER_PLACED_EVENT_TYPE = "ORDER_PLACED";

  private final OrderRepository orderRepository;
  private final OutboxRepository outboxRepository;
  private final OrderMapper orderMapper;
  private final ObjectMapper objectMapper;

  @Transactional
  @Override
  public Order placeOrder(Order order) {
    order.setOrderNumber(generateOrderNumber());
    order.initializeForPlacement();

    OrderEntity savedOrder = orderRepository.save(orderMapper.toEntity(order));
//    OrderEntity savedOrder = orderMapper.toEntity(order);

    OutboxEntity outboxEntity = buildOrderPlacedOutbox(savedOrder);
    outboxRepository.save(outboxEntity);

    return orderMapper.toBusiness(savedOrder);
  }

  private OutboxEntity buildOrderPlacedOutbox(OrderEntity order) {
    OffsetDateTime now = OffsetDateTime.now();
    OutboxEntity outbox = new OutboxEntity();
    outbox.setAggregateType(ORDER_AGGREGATE_TYPE);
    outbox.setAggregateId(String.valueOf(order.getOrderId()));
    outbox.setEventType(ORDER_PLACED_EVENT_TYPE);
    outbox.setPayload(serializePayload(order));
    outbox.setHeaders("{}");
    outbox.setStatus(OutboxStatus.PENDING);
    outbox.setRetryCount(0);
    outbox.setOccurredAt(now);
    outbox.setAvailableAt(now);
    return outbox;
  }

  private String serializePayload(OrderEntity order) {
    Map<String, Object> payload = new LinkedHashMap<>();
    payload.put("eventType", ORDER_PLACED_EVENT_TYPE);
    payload.put("orderId", order.getOrderId());
    payload.put("orderNumber", order.getOrderNumber());
    payload.put("customerId", order.getCustomerId());
    payload.put("status", order.getStatus());
    payload.put("paymentStatus", order.getPaymentStatus());
    payload.put("totalAmount", order.getTotalAmount());
    payload.put("currencyCode", order.getCurrencyCode());
    payload.put("placedAt", order.getPlacedAt());
    try {
      return objectMapper.writeValueAsString(payload);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to serialize outbox payload", e);
    }
  }

  private String generateOrderNumber() {
    return "ORD-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
  }
}
