package org.example.orderservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.orderservice.model.enums.OrderStatus;
import org.example.orderservice.model.enums.PaymentMethod;
import org.example.orderservice.model.enums.PaymentStatus;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long orderId;

  @Column(nullable = false, unique = true, length = 40, updatable = false)
  private String orderNumber;

  @Column(nullable = false, length = 64)
  private String customerId;

  @Column(nullable = false, length = 120)
  private String customerEmail;

  @Column(length = 20)
  private String customerPhone;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 24)
  private OrderStatus status;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 24)
  private PaymentStatus paymentStatus;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 24)
  private PaymentMethod paymentMethod;

  @Column(nullable = false, length = 3)
  private String currencyCode;

  @Column(nullable = false)
  private Integer itemCount;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal subtotalAmount;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal discountAmount;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal taxAmount;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal shippingAmount;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal totalAmount;

  @Column(nullable = false, length = 180)
  private String shippingAddressLine1;

  @Column(length = 180)
  private String shippingAddressLine2;

  @Column(nullable = false, length = 80)
  private String shippingCity;

  @Column(nullable = false, length = 80)
  private String shippingCountry;

  @Column(length = 20)
  private String shippingPostalCode;

  @Column(length = 500)
  private String notes;

  @Column(nullable = false)
  private OffsetDateTime placedAt;

  private OffsetDateTime paidAt;
  private OffsetDateTime shippedAt;
  private OffsetDateTime deliveredAt;
  private OffsetDateTime cancelledAt;

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
    if (placedAt == null) {
      placedAt = now;
    }
    if (status == null) {
      status = OrderStatus.CREATED;
    }
    if (paymentStatus == null) {
      paymentStatus = PaymentStatus.UNPAID;
    }
    if (discountAmount == null) {
      discountAmount = BigDecimal.ZERO;
    }
    if (taxAmount == null) {
      taxAmount = BigDecimal.ZERO;
    }
    if (shippingAmount == null) {
      shippingAmount = BigDecimal.ZERO;
    }
  }

  @PreUpdate
  void preUpdate() {
    updatedAt = OffsetDateTime.now();
  }
}
