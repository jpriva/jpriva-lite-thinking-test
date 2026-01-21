package com.jpriva.orders.application.dto;

import com.jpriva.orders.domain.model.Order;
import com.jpriva.orders.domain.model.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Schema(description = "Order related data transfer objects")
public record OrderDto() {

    @Schema(description = "Request to create a new order")
    public record CreateRequest(
            @Schema(description = "ID of the company that owns the order")
            @NotNull(message = "Company ID is required")
            String companyId,
            @Schema(description = "ID of the client that owns the order")
            @NotNull(message = "Client ID is required")
            UUID clientId,
            @Schema(description = "Currency code for the order", example = "USD")
            @NotNull(message = "Currency code is required")
            String currencyCode
    ) {
    }

    @Schema(description = "Request to add an item to an order")
    public record AddItemRequest(
            @Schema(description = "ID of the product to add")
            @NotNull(message = "Product ID is required")
            UUID productId,
            @Schema(description = "Quantity of the product to add", example = "2")
            @Positive(message = "Quantity must be positive")
            Integer quantity
    ) {}

    @Schema(description = "Request to change the quantity of an item in an order")
    public record ChangeItemQuantityRequest(
            @Schema(description = "ID of the product to update")
            @NotNull(message = "Product ID is required")
            UUID productId,
            @Schema(description = "New quantity of the product", example = "3")
            @Positive(message = "Quantity must be positive")
            @NotNull(message = "Quantity is required")
            Integer quantity
    ) {}

    @Schema(description = "Response containing order details")
    public record Response(
            @Schema(description = "Order's unique identifier")
            UUID id,
            @Schema(description = "ID of the company that owns the order")
            UUID companyId,
            @Schema(description = "ID of the client that placed the order")
            UUID clientId,
            @Schema(description = "Name of the client that placed the order", example = "John Doe")
            String clientName,
            @Schema(description = "Shipping address for the order", example = "123 Main St, Anytown, USA")
            String address,
            @Schema(description = "Date and time the order was placed")
            LocalDateTime orderDate,
            @Schema(description = "Current status of the order", example = "PENDING")
            String status,
            @Schema(description = "Total amount of the order", example = "150.75")
            BigDecimal totalAmount,
            @Schema(description = "Currency of the total amount", example = "USD")
            String currency,
            @Schema(description = "List of items in the order")
            List<ItemResponse> items
    ) {
        public static Response fromDomain(Order order) {
            return new Response(
                    order.getId(),
                    order.getCompanyId(),
                    order.getClientId(),
                    order.getClientName(),
                    order.getAddress(),
                    order.getOrderDate(),
                    order.getStatus().name(),
                    order.getTotalAmount().amount(),
                    order.getTotalAmount().currency().getCode(),
                    order.getItems().stream().map(ItemResponse::fromDomain).collect(Collectors.toList())
            );
        }
    }

    @Schema(description = "Response containing order item details")
    public record ItemResponse(
            @Schema(description = "Order item's unique identifier")
            UUID id,
            @Schema(description = "ID of the product")
            UUID productId,
            @Schema(description = "Name of the product", example = "Laptop")
            String productName,
            @Schema(description = "Quantity of the product", example = "1")
            int quantity,
            @Schema(description = "Unit price of the product", example = "1200.50")
            BigDecimal unitPrice
    ) {
        public static ItemResponse fromDomain(OrderItem item) {
            return new ItemResponse(
                    item.getId(),
                    item.getProductId(),
                    item.getProductName(),
                    item.getQuantity(),
                    item.getUnitPrice().amount()
            );
        }
    }
}
