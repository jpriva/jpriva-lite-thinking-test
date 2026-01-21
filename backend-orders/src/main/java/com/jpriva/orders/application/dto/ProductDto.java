package com.jpriva.orders.application.dto;

import com.jpriva.orders.domain.model.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Schema(description = "Product related data transfer objects")
public record ProductDto() {

    @Schema(description = "Request to create a new product")
    public record CreateRequest(
            @Schema(description = "ID of the company that owns the product")
            @NotNull(message = "Company ID is required")
            String companyId,
            @Schema(description = "ID of the category the product belongs to")
            @NotNull(message = "Category ID is required")
            UUID categoryId,
            @Schema(description = "Product name", example = "Laptop")
            @NotBlank(message = "Name is required")
            String name,
            @Schema(description = "Stock Keeping Unit", example = "LP-12345")
            @NotBlank(message = "SKU is required")
            String sku,
            @Schema(description = "Product description", example = "A powerful laptop for all your needs")
            String description
    ) {}

    @Schema(description = "Request to update the price of a product")
    public record UpdatePriceRequest(
            @Schema(description = "Price of the product", example = "1200.50")
            @NotNull(message = "Price is required")
            @Positive(message = "Price must be positive")
            BigDecimal price,
            @Schema(description = "Currency code for the price", example = "USD")
            @NotBlank(message = "Currency code is required")
            String currencyCode
    ) {}

    @Schema(description = "Response containing product details")
    public record Response(
            @Schema(description = "Product's unique identifier")
            UUID id,
            @Schema(description = "ID of the company that owns the product")
            UUID companyId,
            @Schema(description = "ID of the category the product belongs to")
            UUID categoryId,
            @Schema(description = "Product name", example = "Laptop")
            String name,
            @Schema(description = "Stock Keeping Unit", example = "LP-12345")
            String sku,
            @Schema(description = "Product description", example = "A powerful laptop for all your needs")
            String description,
            @Schema(description = "Quantity in stock", example = "100")
            Integer stockQuantity,
            @Schema(description = "Prices of the product in different currencies")
            Map<String, BigDecimal> prices,
            @Schema(description = "Timestamp of product creation")
            LocalDateTime createdAt
    ) {
        public static Response fromDomain(Product product) {
            Map<String, BigDecimal> pricesMap = product.getPrices().values().stream()
                    .collect(Collectors.toMap(
                            p -> p.getPrice().currency().getCode(),
                            p -> p.getPrice().amount()
                    ));

            return new Response(
                    product.getId(),
                    product.getCompanyId(),
                    product.getCategoryId(),
                    product.getName(),
                    product.getSku(),
                    product.getDescription(),
                    product.getInventory() != null ? product.getInventory().getQuantity() : 0,
                    pricesMap,
                    product.getCreatedAt()
            );
        }
    }
}
