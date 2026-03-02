package org.example.orderservice.repository;

import java.util.List;
import java.util.Optional;
import org.example.orderservice.model.entity.OrderEntity;
import org.example.orderservice.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

  Optional<OrderEntity> findByOrderNumber(String orderNumber);

  boolean existsByOrderNumber(String orderNumber);

  List<OrderEntity> findAllByCustomerIdOrderByCreatedAtDesc(String customerId);

  List<OrderEntity> findAllByStatusOrderByCreatedAtDesc(OrderStatus status);
}
