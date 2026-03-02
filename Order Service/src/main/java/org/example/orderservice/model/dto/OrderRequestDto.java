package org.example.orderservice.model.dto;

import java.math.BigDecimal;
import lombok.Builder;
import org.example.orderservice.model.enums.PaymentMethod;

@Builder
public record OrderRequestDto(
    String customerId,
    String customerEmail,
    String customerPhone,
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
    String notes) {}
