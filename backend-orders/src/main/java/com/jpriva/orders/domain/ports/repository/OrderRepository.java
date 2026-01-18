package com.jpriva.orders.domain.ports.repository;

import com.jpriva.orders.domain.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(UUID id);
    List<Order> findAll();
    Page<Order> findByCompanyId(Pageable pageable, UUID companyId);
    Page<Order> findByClientIdAndCompanyId(UUID clientId, UUID companyId, Pageable pageable);
    void deleteById(UUID id);
}
