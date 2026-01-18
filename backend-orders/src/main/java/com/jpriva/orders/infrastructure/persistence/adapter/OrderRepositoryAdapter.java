package com.jpriva.orders.infrastructure.persistence.adapter;

import com.jpriva.orders.domain.model.Order;
import com.jpriva.orders.domain.ports.repository.OrderRepository;
import com.jpriva.orders.infrastructure.persistence.entity.OrderEntity;
import com.jpriva.orders.infrastructure.persistence.mapper.OrderMapper;
import com.jpriva.orders.infrastructure.persistence.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository jpaRepository;

    @Override
    public Order save(Order order) {
        OrderEntity entity = OrderMapper.toEntity(order);
        OrderEntity saved = jpaRepository.save(entity);
        return OrderMapper.toDomain(saved);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return jpaRepository.findById(id).map(OrderMapper::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return jpaRepository.findAll().stream()
                .map(OrderMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Order> findByCompanyId(Pageable pageable, UUID companyId) {
        return jpaRepository.findByCompanyId(companyId, pageable)
                .map(OrderMapper::toDomain);
    }

    @Override
    public Page<Order> findByClientIdAndCompanyId(UUID clientId, UUID companyId, Pageable pageable) {
        return jpaRepository.findByClientIdAndCompanyId(clientId, companyId, pageable).map(OrderMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
