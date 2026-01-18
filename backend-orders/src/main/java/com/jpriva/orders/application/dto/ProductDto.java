package com.jpriva.orders.application.dto;

import com.jpriva.orders.domain.model.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public record ProductDto() {

    public record CreateRequest(
            @NotNull(message = "Company ID is required")
            String companyId,
            @NotNull(message = "Category ID is required")
            UUID categoryId,
            @NotBlank(message = "Name is required")
            String name,
            @NotBlank(message = "SKU is required")
            String sku,
            String description
    ) {}

    public record UpdatePriceRequest(
            @NotNull(message = "Price is required")
            @Positive(message = "Price must be positive")
            BigDecimal price,
            @NotBlank(message = "Currency code is required")
            String currencyCode
    ) {}

    public record Response(
            UUID id,
            UUID companyId,
            UUID categoryId,
            String name,
            String sku,
            String description,
            Integer stockQuantity,
            Map<String, BigDecimal> prices,
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
