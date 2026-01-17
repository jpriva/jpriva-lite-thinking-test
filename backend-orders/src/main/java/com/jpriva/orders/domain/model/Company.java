package com.jpriva.orders.domain.model;

import com.jpriva.orders.domain.exceptions.CompanyErrorCodes;
import com.jpriva.orders.domain.exceptions.DomainException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Company {

    private final UUID id;
    private String name;
    private String taxId;
    private String address;
    private String phone;
    private final LocalDateTime createdAt;

    @Builder
    public Company(UUID id, String name, String taxId, String address, String phone, LocalDateTime createdAt) {
        if (id == null) {
            throw new DomainException(CompanyErrorCodes.COMPANY_ID_NULL);
        }
        this.id = id;
        this.createdAt = createdAt;
        changeName(name);
        changeTaxId(taxId);
        changeAddress(address);
        changePhone(phone);
    }

    public static Company create(String name, String taxId, String address, String phone) {
        return Company.builder()
                .id(UUID.randomUUID())
                .name(name)
                .taxId(taxId)
                .address(address)
                .phone(phone)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Company fromPersistence(UUID id, String name, String taxId, String address, String phone, LocalDateTime createdAt) {
        try {
            return Company.builder()
                    .id(id)
                    .name(name)
                    .taxId(taxId)
                    .address(address)
                    .phone(phone)
                    .createdAt(createdAt)
                    .build();
        } catch (DomainException e) {
            throw new DomainException(CompanyErrorCodes.COMPANY_PERSISTENCE_ERROR, e);
        }
    }

    public void changeName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException(CompanyErrorCodes.COMPANY_NAME_NULL);
        }
        this.name = name.trim();
    }

    public void changeTaxId(String taxId) {
        if (taxId == null || taxId.isBlank()) {
            throw new DomainException(CompanyErrorCodes.COMPANY_TAX_ID_NULL);
        }
        this.taxId = taxId.trim();
    }

    public void changeAddress(String address) {
        if (address == null || address.isBlank()) {
            this.address = null;
            return;
        }
        this.address = address.trim();
    }

    public void changePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            this.phone = null;
            return;
        }
        this.phone = phone.trim();
    }
}
