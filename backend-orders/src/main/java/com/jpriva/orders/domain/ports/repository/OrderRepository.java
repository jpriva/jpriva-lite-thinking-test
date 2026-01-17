package com.jpriva.orders.domain.ports.repository;

import com.jpriva.orders.domain.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(UUID id);
    List<Order> findAll();
    List<Order> findByCompanyId(UUID companyId);
    List<Order> findByClientId(UUID clientId);
    void deleteById(UUID id);
}
