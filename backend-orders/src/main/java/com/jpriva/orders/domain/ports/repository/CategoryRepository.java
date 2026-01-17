package com.jpriva.orders.domain.ports.repository;

import com.jpriva.orders.domain.model.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {
    Category save(Category category);
    Optional<Category> findById(UUID id);
    List<Category> findByCompanyId(UUID companyId);
    List<Category> findAll();
    void deleteById(UUID id);
}
