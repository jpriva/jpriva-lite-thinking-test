package com.jpriva.orders.infrastructure.persistence.repository;

import com.jpriva.orders.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {
    Page<OrderEntity> findByCompanyId(UUID companyId, Pageable pageable);
    List<OrderEntity> findByClientId(UUID clientId);
    Page<OrderEntity> findByClientIdAndCompanyId(UUID clientId, UUID companyId, Pageable pageable);
}
