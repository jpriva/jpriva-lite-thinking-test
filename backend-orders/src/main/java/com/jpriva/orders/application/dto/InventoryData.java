package com.jpriva.orders.application.dto;

import com.jpriva.orders.domain.model.Product;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Inventory data for a set of products")
public record InventoryData(
        @Schema(description = "Original name of the inventory data file", example = "inventory.csv")
        String originalName,
        @Schema(description = "Sanitized name of the inventory data file", example = "inventory_1678886400000.csv")
        String sanitizedName,
        @Schema(description = "List of products in the inventory")
        List<Product> products
) {}