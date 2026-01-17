package com.jpriva.orders.infrastructure.persistence.mapper;

import com.jpriva.orders.domain.model.Category;
import com.jpriva.orders.infrastructure.persistence.entity.CategoryEntity;

public class CategoryMapper {

    private CategoryMapper() {}

    public static Category toDomain(CategoryEntity entity) {
        if (entity == null) return null;
        return Category.fromPersistence(
                entity.getId(),
                entity.getCompanyId(),
                entity.getName(),
                entity.getDescription()
        );
    }

    public static CategoryEntity toEntity(Category domain) {
        if (domain == null) return null;
        return CategoryEntity.builder()
                .id(domain.getId())
                .companyId(domain.getCompanyId())
                .name(domain.getName())
                .description(domain.getDescription())
                .build();
    }
}
