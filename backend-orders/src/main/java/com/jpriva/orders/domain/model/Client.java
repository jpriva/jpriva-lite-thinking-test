package com.jpriva.orders.domain.model;

import com.jpriva.orders.domain.exceptions.ClientErrorCodes;
import com.jpriva.orders.domain.exceptions.DomainException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Client {

    private final UUID id;
    private final UUID companyId;
    private final UUID userId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private final LocalDateTime createdAt;

    @Builder
    public Client(UUID id, UUID companyId, UUID userId, String name, String email, String phone, String address, LocalDateTime createdAt) {
        if (id == null) {
            throw new DomainException(ClientErrorCodes.CLIENT_ID_NULL);
        }
        if (companyId == null) {
            throw new DomainException(ClientErrorCodes.CLIENT_COMPANY_ID_NULL);
        }
        if (userId == null) {
            throw new DomainException(ClientErrorCodes.CLIENT_USER_ID_NULL);
        }
        
        this.id = id;
        this.companyId = companyId;
        this.userId = userId;
        this.createdAt = createdAt;
        changeName(name);
        changeEmail(email);
        changePhone(phone);
        changeAddress(address);
    }

    public static Client create(UUID companyId, UUID userId, String name, String email, String phone, String address) {
        return Client.builder()
                .id(UUID.randomUUID())
                .companyId(companyId)
                .userId(userId)
                .name(name)
                .email(email)
                .phone(phone)
                .address(address)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Client fromPersistence(UUID id, UUID companyId, UUID userId, String name, String email, String phone, String address, LocalDateTime createdAt) {
        try {
            return Client.builder()
                    .id(id)
                    .companyId(companyId)
                    .userId(userId)
                    .name(name)
                    .email(email)
                    .phone(phone)
                    .address(address)
                    .createdAt(createdAt)
                    .build();
        } catch (DomainException e) {
            throw new DomainException(ClientErrorCodes.CLIENT_PERSISTENCE_ERROR, e);
        }
    }

    public void changeName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException(ClientErrorCodes.CLIENT_NAME_NULL);
        }
        this.name = name.trim();
    }

    public void changeEmail(String email) {
        if (email == null || email.isBlank()) {
            this.email = null;
            return;
        }
        this.email = email.trim();
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
