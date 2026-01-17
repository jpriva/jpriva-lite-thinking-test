package com.jpriva.orders.infrastructure.persistence.adapter;

import com.jpriva.orders.domain.model.Product;
import com.jpriva.orders.domain.ports.repository.ProductRepository;
import com.jpriva.orders.infrastructure.persistence.entity.ProductEntity;
import com.jpriva.orders.infrastructure.persistence.mapper.ProductMapper;
import com.jpriva.orders.infrastructure.persistence.repository.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepository {

    private final ProductJpaRepository jpaRepository;

    @Override
    public Product save(Product product) {
        ProductEntity entity = ProductMapper.toEntity(product);
        ProductEntity saved = jpaRepository.save(entity);
        return ProductMapper.toDomain(saved);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return jpaRepository.findById(id).map(ProductMapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return jpaRepository.findAll().stream()
                .map(ProductMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByCompanyId(UUID companyId) {
        return jpaRepository.findByCompanyId(companyId).stream()
                .map(ProductMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Product> findBySkuAndCompanyId(String sku, UUID companyId) {
        return jpaRepository.findBySkuAndCompanyId(sku, companyId).map(ProductMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Map<UUID,Product> findByIds(Set<UUID> productIds){
        return jpaRepository.findByIdIn(productIds).stream()
                .map(ProductMapper::toDomain)
                .collect(Collectors.toMap(Product::getId, p -> p));
    }
}
