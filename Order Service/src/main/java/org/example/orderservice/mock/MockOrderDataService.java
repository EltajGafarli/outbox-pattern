package org.example.orderservice.mock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.datafaker.Faker;
import org.example.orderservice.model.dto.OrderRequestDto;
import org.example.orderservice.model.enums.PaymentMethod;
import org.springframework.stereotype.Service;

@Service
public class MockOrderDataService {

  private static final String CURRENCY = "USD";
  private final Faker faker = new Faker();

  public OrderRequestDto generateOrderRequest() {
    int itemCount = ThreadLocalRandom.current().nextInt(1, 8);
    BigDecimal subtotal = randomMoney(20, 500);
    BigDecimal discount = randomMoney(0, 40);
    BigDecimal tax = subtotal.subtract(discount).multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);
    BigDecimal shipping = randomMoney(0, 25);
    BigDecimal total = subtotal.subtract(discount).add(tax).add(shipping).setScale(2, RoundingMode.HALF_UP);

    return OrderRequestDto.builder()
        .customerId(faker.idNumber().valid())
        .customerEmail(faker.internet().emailAddress())
        .customerPhone(faker.phoneNumber().phoneNumber())
        .paymentMethod(randomPaymentMethod())
        .currencyCode(CURRENCY)
        .itemCount(itemCount)
        .subtotalAmount(subtotal)
        .discountAmount(discount)
        .taxAmount(tax)
        .shippingAmount(shipping)
        .totalAmount(total)
        .shippingAddressLine1(faker.address().streetAddress())
        .shippingAddressLine2(faker.address().secondaryAddress())
        .shippingCity(faker.address().city())
        .shippingCountry(faker.address().countryCode())
        .shippingPostalCode(faker.address().postcode())
        .notes(faker.lorem().sentence(8))
        .build();
  }

  public List<OrderRequestDto> generateOrderRequests(int count) {
    int safeCount = Math.max(1, count);
    List<OrderRequestDto> requests = new ArrayList<>(safeCount);
    for (int i = 0; i < safeCount; i++) {
      requests.add(generateOrderRequest());
    }
    return requests;
  }

  private PaymentMethod randomPaymentMethod() {
    PaymentMethod[] methods = PaymentMethod.values();
    return methods[ThreadLocalRandom.current().nextInt(methods.length)];
  }

  private BigDecimal randomMoney(int min, int max) {
    double value = ThreadLocalRandom.current().nextDouble(min, max + 1);
    return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
  }
}
