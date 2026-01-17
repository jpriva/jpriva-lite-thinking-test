package com.jpriva.orders.infrastructure.persistence.mapper;

import com.jpriva.orders.domain.model.Company;
import com.jpriva.orders.infrastructure.persistence.entity.CompanyEntity;

public class CompanyMapper {

    private CompanyMapper() {}

    public static Company toDomain(CompanyEntity entity) {
        if (entity == null) return null;
        return Company.fromPersistence(
                entity.getId(),
                entity.getName(),
                entity.getTaxId(),
                entity.getAddress(),
                entity.getPhone(),
                entity.getCreatedAt()
        );
    }

    public static CompanyEntity toEntity(Company domain) {
        if (domain == null) return null;
        return CompanyEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .taxId(domain.getTaxId())
                .address(domain.getAddress())
                .phone(domain.getPhone())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
