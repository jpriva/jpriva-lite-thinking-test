package com.jpriva.orders.application.dto;

import com.jpriva.orders.domain.model.Company;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.UUID;

public record CompanyDto() {

    public record CreateRequest(
            @NotBlank(message = "Name is required")
            String name,
            @NotBlank(message = "Tax ID is required")
            String taxId,
            String address,
            String phone
    ) {
        public Company toDomain() {
            return Company.create(name, taxId, address, phone);
        }
    }

    public record Response(
            UUID id,
            String name,
            String taxId,
            String address,
            String phone,
            LocalDateTime createdAt
    ) {
        public static Response fromDomain(Company company) {
            return new Response(
                    company.getId(),
                    company.getName(),
                    company.getTaxId(),
                    company.getAddress(),
                    company.getPhone(),
                    company.getCreatedAt()
            );
        }
    }
}
