package com.jpriva.orders.application.dto;

import com.jpriva.orders.domain.model.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Category related data transfer objects")
public record CategoryDto() {

    @Schema(description = "Request to create a new category")
    public record CreateRequest(
            @Schema(description = "ID of the company that owns the category")
            @NotNull(message = "Company ID is required")
            String companyId,
            @Schema(description = "Category name", example = "Electronics")
            @NotBlank(message = "Name is required")
            String name,
            @Schema(description = "Category description", example = "Electronic devices and accessories")
            String description
    ) {
    }

    @Schema(description = "Response containing category details")
    public record Response(
            @Schema(description = "Category's unique identifier")
            UUID id,
            @Schema(description = "ID of the company that owns the category")
            UUID companyId,
            @Schema(description = "Category name", example = "Electronics")
            String name,
            @Schema(description = "Category description", example = "Electronic devices and accessories")
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
