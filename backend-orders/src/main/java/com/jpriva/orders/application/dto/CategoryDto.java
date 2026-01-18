package com.jpriva.orders.application.dto;

import com.jpriva.orders.domain.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CategoryDto() {

    public record CreateRequest(
            @NotNull(message = "Company ID is required")
            String companyId,
            @NotBlank(message = "Name is required")
            String name,
            String description
    ) {
        }

    public record Response(
            UUID id,
            UUID companyId,
            String name,
            String description
    ) {
        public static Response fromDomain(Category category) {
            return new Response(
                    category.getId(),
                    category.getCompanyId(),
                    category.getName(),
                    category.getDescription()
            );
        }
    }
}
