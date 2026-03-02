package org.example.orderservice.service;

import org.example.orderservice.model.business.Order;

public interface OrderService {

  Order placeOrder(Order order);
}
