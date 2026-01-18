package com.jpriva.orders.application.dto;

import com.jpriva.orders.domain.model.Client;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClientDto() {

    public record CreateRequest(
            @NotNull(message = "Company ID is required")
            String companyId,
            @NotBlank(message = "Name is required")
            String name,
            String email,
            String phone,
            String address
    ) {}

    public record Response(
            UUID id,
            UUID companyId,
            String name,
            String email,
            String phone,
            String address,
            LocalDateTime createdAt
    ) {
        public static Response fromDomain(Client client) {
            return new Response(
                    client.getId(),
                    client.getCompanyId(),
                    client.getName(),
                    client.getEmail(),
                    client.getPhone(),
                    client.getAddress(),
                    client.getCreatedAt()
            );
        }
    }
}
