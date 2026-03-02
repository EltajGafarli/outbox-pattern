package org.example.orderservice.facade.impl;

import lombok.RequiredArgsConstructor;
import org.example.orderservice.facade.OrderFacade;
import org.example.orderservice.mapper.OrderMapper;
import org.example.orderservice.model.business.Order;
import org.example.orderservice.model.dto.OrderRequestDto;
import org.example.orderservice.model.dto.OrderResponseDto;
import org.example.orderservice.service.OrderService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderFacadeImpl implements OrderFacade {

  private final OrderService orderService;
  private final OrderMapper orderMapper;

  @Override
  public OrderResponseDto placeOrder(OrderRequestDto request) {


    Order order = orderMapper.toBusiness(request);
    Order savedOrder = orderService.placeOrder(order);
    return orderMapper.toResponse(savedOrder);
  }
}
