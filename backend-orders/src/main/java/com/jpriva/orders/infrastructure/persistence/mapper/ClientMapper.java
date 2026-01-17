package com.jpriva.orders.infrastructure.persistence.mapper;

import com.jpriva.orders.domain.model.Client;
import com.jpriva.orders.infrastructure.persistence.entity.ClientEntity;

public class ClientMapper {

    private ClientMapper() {}

    public static Client toDomain(ClientEntity entity) {
        if (entity == null) return null;
        return Client.fromPersistence(
                entity.getId(),
                entity.getCompanyId(),
                entity.getUserId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getAddress(),
                entity.getCreatedAt()
        );
    }

    public static ClientEntity toEntity(Client domain) {
        if (domain == null) return null;
        return ClientEntity.builder()
                .id(domain.getId())
                .companyId(domain.getCompanyId())
                .userId(domain.getUserId())
                .name(domain.getName())
                .email(domain.getEmail())
                .phone(domain.getPhone())
                .address(domain.getAddress())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
