package org.example.orderservice.facade;

import org.example.orderservice.model.dto.OrderRequestDto;
import org.example.orderservice.model.dto.OrderResponseDto;

public interface OrderFacade {

  OrderResponseDto placeOrder(OrderRequestDto request);
}
