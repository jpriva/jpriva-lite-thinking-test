package com.jpriva.orders.application.dto;

import com.jpriva.orders.domain.model.Company;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Company related data transfer objects")
public record CompanyDto() {

    @Schema(description = "Request to create a new company")
    public record CreateRequest(
            @Schema(description = "Company's name", example = "ACME Corporation")
            @NotBlank(message = "Name is required")
            String name,
            @Schema(description = "Company's tax identifier", example = "123456789")
            @NotBlank(message = "Tax ID is required")
            String taxId,
            @Schema(description = "Company's address", example = "456 Business Rd, Big City, USA")
            String address,
            @Schema(description = "Company's phone number", example = "+0987654321")
            String phone
    ) {
        public Company toDomain() {
            return Company.create(name, taxId, address, phone);
        }
    }

    @Schema(description = "Response containing company details")
    public record Response(
            @Schema(description = "Company's unique identifier")
            UUID id,
            @Schema(description = "Company's name", example = "ACME Corporation")
            String name,
            @Schema(description = "Company's tax identifier", example = "123456789")
            String taxId,
            @Schema(description = "Company's address", example = "456 Business Rd, Big City, USA")
            String address,
            @Schema(description = "Company's phone number", example = "+0987654321")
            String phone,
            @Schema(description = "Timestamp of company creation")
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
