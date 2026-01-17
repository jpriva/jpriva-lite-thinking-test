package com.jpriva.orders.infrastructure.persistence.repository;

import com.jpriva.orders.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {
    List<OrderEntity> findByCompanyId(UUID companyId);
    List<OrderEntity> findByClientId(UUID clientId);
}
