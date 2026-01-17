package com.jpriva.orders.infrastructure.persistence.mapper;

import com.jpriva.orders.domain.model.User;
import com.jpriva.orders.infrastructure.persistence.entity.UserEntity;
import com.jpriva.orders.domain.model.vo.Role;

public class UserMapper {

    private UserMapper() {}

    public static User toDomain(UserEntity entity) {
        if (entity == null) return null;
        return User.fromPersistence(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getFullName(),
                entity.getPhone(),
                entity.getAddress(),
                Role.valueOf(entity.getRole()),
                entity.getCreatedAt()
        );
    }

    public static UserEntity toEntity(User domain) {
        if (domain == null) return null;
        return UserEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .passwordHash(domain.getPasswordHash())
                .fullName(domain.getFullName())
                .phone(domain.getPhone())
                .address(domain.getAddress())
                .role(domain.getRole().name())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
