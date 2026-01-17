package com.jpriva.orders.infrastructure.persistence.mapper;

import com.jpriva.orders.domain.model.Order;
import com.jpriva.orders.domain.model.OrderItem;
import com.jpriva.orders.domain.model.vo.Money;
import com.jpriva.orders.domain.model.vo.OrderStatus;
import com.jpriva.orders.infrastructure.persistence.entity.OrderEntity;
import com.jpriva.orders.infrastructure.persistence.entity.OrderItemEntity;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    private OrderMapper() {}

    public static Order toDomain(OrderEntity entity) {
        if (entity == null) return null;

        List<OrderItem> items = entity.getItems().stream()
                .map(item -> OrderItem.fromPersistence(
                        item.getId(),
                        item.getOrder().getId(),
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        Money.fromString(entity.getCurrencyCode(), item.getUnitPrice().toString())
                ))
                .collect(Collectors.toList());

        return new Order(
                entity.getId(),
                entity.getCompanyId(),
                entity.getClientId(),
                entity.getClientName(),
                entity.getAddress(),
                entity.getOrderDate(),
                OrderStatus.valueOf(entity.getStatus()),
                Money.fromString(entity.getCurrencyCode(), entity.getTotalAmount().toString()),
                items
        );
    }

    public static OrderEntity toEntity(Order domain) {
        if (domain == null) return null;

        OrderEntity entity = OrderEntity.builder()
                .id(domain.getId())
                .companyId(domain.getCompanyId())
                .clientId(domain.getClientId())
                .clientName(domain.getClientName())
                .address(domain.getAddress())
                .orderDate(domain.getOrderDate())
                .status(domain.getStatus().name())
                .currencyCode(domain.getTotalAmount().currency().getCode())
                .totalAmount(domain.getTotalAmount().amount())
                .build();

        if (domain.getItems() != null) {
            entity.setItems(domain.getItems().stream()
                    .map(item -> OrderItemEntity.builder()
                            .id(item.getId())
                            .order(entity) // Set parent
                            .productId(item.getProductId())
                            .productName(item.getProductName())
                            .quantity(item.getQuantity())
                            .unitPrice(item.getUnitPrice().amount())
                            .build())
                    .collect(Collectors.toList()));
        }

        return entity;
    }
}
