package org.example.orderservice.model.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.Builder;
import org.example.orderservice.model.enums.OrderStatus;
import org.example.orderservice.model.enums.PaymentMethod;
import org.example.orderservice.model.enums.PaymentStatus;

@Builder
public record OrderResponseDto(
    Long orderId,
    String orderNumber,
    String customerId,
    String customerEmail,
    String customerPhone,
    OrderStatus status,
    PaymentStatus paymentStatus,
    PaymentMethod paymentMethod,
    String currencyCode,
    Integer itemCount,
    BigDecimal subtotalAmount,
    BigDecimal discountAmount,
    BigDecimal taxAmount,
    BigDecimal shippingAmount,
    BigDecimal totalAmount,
    String shippingAddressLine1,
    String shippingAddressLine2,
    String shippingCity,
    String shippingCountry,
    String shippingPostalCode,
    String notes,
    OffsetDateTime placedAt,
    OffsetDateTime paidAt,
    OffsetDateTime shippedAt,
    OffsetDateTime deliveredAt,
    OffsetDateTime cancelledAt,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {}
