package org.example.orderservice.mapper;

import org.example.orderservice.model.business.Order;
import org.example.orderservice.model.dto.OrderRequestDto;
import org.example.orderservice.model.dto.OrderResponseDto;
import org.example.orderservice.model.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface OrderMapper {

  @Mapping(target = "orderId", ignore = true)
  @Mapping(target = "orderNumber", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "paymentStatus", ignore = true)
  @Mapping(target = "placedAt", ignore = true)
  @Mapping(target = "paidAt", ignore = true)
  @Mapping(target = "shippedAt", ignore = true)
  @Mapping(target = "deliveredAt", ignore = true)
  @Mapping(target = "cancelledAt", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Order toBusiness(OrderRequestDto request);

  OrderEntity toEntity(Order order);

  Order toBusiness(OrderEntity entity);

  OrderResponseDto toResponse(Order order);

  OrderResponseDto toResponse(OrderEntity entity);

  @Mapping(target = "orderId", ignore = true)
  @Mapping(target = "orderNumber", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "paymentStatus", ignore = true)
  @Mapping(target = "placedAt", ignore = true)
  @Mapping(target = "paidAt", ignore = true)
  @Mapping(target = "shippedAt", ignore = true)
  @Mapping(target = "deliveredAt", ignore = true)
  @Mapping(target = "cancelledAt", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateBusinessFromRequest(OrderRequestDto request, @MappingTarget Order order);
}
