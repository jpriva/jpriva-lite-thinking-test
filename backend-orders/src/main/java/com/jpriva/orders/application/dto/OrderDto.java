package com.jpriva.orders.application.dto;

import com.jpriva.orders.domain.model.Order;
import com.jpriva.orders.domain.model.OrderItem;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record OrderDto() {

    public record CreateByUser(
            @NotNull(message = "Company ID is required")
            UUID companyId,
            @NotNull(message = "User ID is required")
            UUID userId,
            @NotNull(message = "Currency code is required")
            String currencyCode
    ) {
    }
    public record CreateByAdmin(
            @NotNull(message = "Company ID is required")
            UUID companyId,
            @NotNull(message = "Client ID is required")
            UUID clientId,
            @NotNull(message = "Currency code is required")
            String currencyCode
    ) {
    }

    public record AddItemRequest(
            @NotNull(message = "Product ID is required")
            UUID productId,
            @Positive(message = "Quantity must be positive")
            Integer quantity
    ) {}

    public record ChangeItemQuantityRequest(
            @NotNull(message = "Product ID is required")
            UUID productId,
            @Positive(message = "Quantity must be positive")
            @NotNull(message = "Quantity is required")
            Integer quantity
    ) {}

    public record Response(
            UUID id,
            UUID companyId,
            UUID clientId,
            String clientName,
            String address,
            LocalDateTime orderDate,
            String status,
            BigDecimal totalAmount,
            String currency,
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

    public record ItemResponse(
            UUID id,
            UUID productId,
            String productName,
            int quantity,
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
