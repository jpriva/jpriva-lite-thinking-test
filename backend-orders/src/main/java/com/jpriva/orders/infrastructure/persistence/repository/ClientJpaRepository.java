package com.jpriva.orders.infrastructure.persistence.repository;

import com.jpriva.orders.infrastructure.persistence.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientJpaRepository extends JpaRepository<ClientEntity, UUID> {
    List<ClientEntity> findByCompanyId(UUID companyId);
}
