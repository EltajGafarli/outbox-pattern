package org.example.orderservice.model.business;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.orderservice.model.enums.OrderStatus;
import org.example.orderservice.model.enums.PaymentMethod;
import org.example.orderservice.model.enums.PaymentStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

  private Long orderId;
  private String orderNumber;

  private String customerId;
  private String customerEmail;
  private String customerPhone;

  private OrderStatus status;
  private PaymentStatus paymentStatus;
  private PaymentMethod paymentMethod;

  private String currencyCode;
  private Integer itemCount;

  private BigDecimal subtotalAmount;
  private BigDecimal discountAmount;
  private BigDecimal taxAmount;
  private BigDecimal shippingAmount;
  private BigDecimal totalAmount;

  private String shippingAddressLine1;
  private String shippingAddressLine2;
  private String shippingCity;
  private String shippingCountry;
  private String shippingPostalCode;

  private String notes;

  private OffsetDateTime placedAt;
  private OffsetDateTime paidAt;
  private OffsetDateTime shippedAt;
  private OffsetDateTime deliveredAt;
  private OffsetDateTime cancelledAt;

  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;

  public void initializeForPlacement() {
    OffsetDateTime now = OffsetDateTime.now();
    if (status == null) {
      status = OrderStatus.CREATED;
    }
    if (paymentStatus == null) {
      paymentStatus = PaymentStatus.UNPAID;
    }
    if (placedAt == null) {
      placedAt = now;
    }
    if (createdAt == null) {
      createdAt = now;
    }
    updatedAt = now;
    recalculateTotal();
  }

  public void recalculateTotal() {
    BigDecimal subtotal = defaultZero(subtotalAmount);
    BigDecimal discount = defaultZero(discountAmount);
    BigDecimal tax = defaultZero(taxAmount);
    BigDecimal shipping = defaultZero(shippingAmount);
    totalAmount = subtotal.subtract(discount).add(tax).add(shipping);
  }

  public void confirm() {
    status = OrderStatus.CONFIRMED;
    updatedAt = OffsetDateTime.now();
  }

  public void markPaid() {
    paymentStatus = PaymentStatus.PAID;
    paidAt = OffsetDateTime.now();
    updatedAt = paidAt;
  }

  public void ship() {
    status = OrderStatus.SHIPPED;
    shippedAt = OffsetDateTime.now();
    updatedAt = shippedAt;
  }

  public void deliver() {
    status = OrderStatus.DELIVERED;
    deliveredAt = OffsetDateTime.now();
    updatedAt = deliveredAt;
  }

  public void cancel() {
    status = OrderStatus.CANCELLED;
    cancelledAt = OffsetDateTime.now();
    updatedAt = cancelledAt;
  }

  private BigDecimal defaultZero(BigDecimal value) {
    return value == null ? BigDecimal.ZERO : value;
  }
}
