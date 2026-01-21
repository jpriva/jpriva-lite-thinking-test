package com.jpriva.orders.application.dto;

import com.jpriva.orders.domain.model.Client;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Client related data transfer objects")
public record ClientDto() {

    @Schema(description = "Request to create a new client")
    public record CreateRequest(
            @Schema(description = "ID of the company that owns the client")
            @NotNull(message = "Company ID is required")
            String companyId,
            @Schema(description = "Client's name", example = "John Doe")
            @NotBlank(message = "Name is required")
            String name,
            @Schema(description = "Client's email address", example = "john.doe@mail.com")
            String email,
            @Schema(description = "Client's phone number", example = "+1234567890")
            String phone,
            @Schema(description = "Client's address", example = "123 Main St, Anytown, USA")
            String address
    ) {}

    @Schema(description = "Response containing client details")
    public record Response(
            @Schema(description = "Client's unique identifier")
            UUID id,
            @Schema(description = "ID of the company that owns the client")
            UUID companyId,
            @Schema(description = "Client's name", example = "John Doe")
            String name,
            @Schema(description = "Client's email address", example = "john.doe@mail.com")
            String email,
            @Schema(description = "Client's phone number", example = "+1234567890")
            String phone,
            @Schema(description = "Client's address", example = "123 Main St, Anytown, USA")
            String address,
            @Schema(description = "Timestamp of client creation")
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
