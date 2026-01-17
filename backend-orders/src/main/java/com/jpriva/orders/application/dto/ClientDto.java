package com.jpriva.orders.application.dto;

import com.jpriva.orders.domain.model.Client;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClientDto() {

    public record CreateRequest(
            @NotNull(message = "Company ID is required")
            UUID companyId,
            @NotNull(message = "User ID is required")
            UUID userId,
            @NotBlank(message = "Name is required")
            String name,
            String email,
            String phone,
            String address
    ) {
        public Client toDomain() {
            return Client.create(companyId, userId, name, email, phone, address);
        }
    }

    public record Response(
            UUID id,
            UUID companyId,
            UUID userId,
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
                    client.getUserId(),
                    client.getName(),
                    client.getEmail(),
                    client.getPhone(),
                    client.getAddress(),
                    client.getCreatedAt()
            );
        }
    }
}
