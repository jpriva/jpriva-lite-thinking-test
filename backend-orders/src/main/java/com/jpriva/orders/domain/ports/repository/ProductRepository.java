package com.jpriva.orders.domain.ports.repository;

import com.jpriva.orders.domain.model.Product;

import java.util.*;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(UUID id);
    List<Product> findAll();
    List<Product> findByCompanyId(UUID companyId);
    Optional<Product> findBySkuAndCompanyId(String sku, UUID companyId);
    void deleteById(UUID id);

    Map<UUID,Product> findByIds(Set<UUID> productIds);
}
