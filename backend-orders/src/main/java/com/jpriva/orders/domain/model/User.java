package com.jpriva.orders.domain.model;

import com.jpriva.orders.domain.exceptions.DomainException;
import com.jpriva.orders.domain.exceptions.UserErrorCodes;
import com.jpriva.orders.domain.model.vo.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class User {

    private final UUID id;
    private final String email;
    private final String passwordHash;
    private final Role role;
    private final LocalDateTime createdAt;
    private String fullName;
    private String phone;
    private String address;

    @Builder
    public User(UUID id, String email, String passwordHash, String fullName, String phone, String address, Role role, LocalDateTime createdAt) {
        if (id == null) {
            throw new DomainException(UserErrorCodes.USER_ID_NULL);
        }
        if (email == null || email.isBlank()) {
            throw new DomainException(UserErrorCodes.USER_EMAIL_NULL);
        }
        if (role == null) {
            throw new DomainException(UserErrorCodes.USER_ROLE_NULL);
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new DomainException(UserErrorCodes.USER_PASSWORD_NULL);
        }
        changeFullName(fullName);
        changePhone(phone);
        changeAddress(address);
        this.id = id;
        this.email = email.trim();
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = createdAt;
    }

    public static User create(String email, String passwordHash, String fullName, String phone, String address, Role role) {
        return User.builder()
                .id(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .email(email)
                .passwordHash(passwordHash)
                .fullName(fullName)
                .phone(phone)
                .address(address)
                .role(role)
                .build();
    }

    public static User fromPersistence(UUID id, String email, String passwordHash, String fullName, String phone, String address, Role role, LocalDateTime createdAt) {
        try {
            return User.builder()
                    .id(id)
                    .email(email)
                    .passwordHash(passwordHash)
                    .fullName(fullName)
                    .phone(phone)
                    .address(address)
                    .role(role)
                    .createdAt(createdAt)
                    .build();
        } catch (DomainException e) {
            throw new DomainException(UserErrorCodes.USER_PERSISTENCE_ERROR, e);
        }
    }

    public void changeFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new DomainException(UserErrorCodes.USER_NAME_NULL);
        }
        this.fullName = fullName.trim();
    }

    public void changePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            this.phone = null;
            return;
        }
        this.phone = phone.trim();
    }

    public void changeAddress(String address) {
        if (address == null || address.isBlank()) {
            this.address = null;
            return;
        }
        this.address = address.trim();
    }
}