package com.jpriva.orders.infrastructure.persistence.repository;

import com.jpriva.orders.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, UUID> {
    List<ProductEntity> findByCompanyId(UUID companyId);

    Optional<ProductEntity> findBySkuAndCompanyId(String sku, UUID companyId);

    List<ProductEntity> findByIdIn(Set<UUID> productIds);
}
