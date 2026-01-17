package com.jpriva.orders.domain.model;

import com.jpriva.orders.domain.exceptions.DomainException;
import com.jpriva.orders.domain.exceptions.ProductErrorCodes;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Inventory {
    private final UUID id;
    private final UUID productId;
    private Integer quantity;
    private LocalDateTime lastUpdated;

    @Builder
    public Inventory(UUID id, UUID productId, Integer quantity, LocalDateTime lastUpdated) {
        if (id == null) {
            throw new DomainException(ProductErrorCodes.INVENTORY_ID_NULL);
        }
        if (productId == null) {
            throw new DomainException(ProductErrorCodes.PRODUCT_ID_NULL);
        }
        if (quantity == null || quantity < 0) {
            throw new DomainException(ProductErrorCodes.INVENTORY_QUANTITY_NEGATIVE);
        }

        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.lastUpdated = lastUpdated != null ? lastUpdated : LocalDateTime.now();
    }

    public static Inventory create(UUID productId, Integer quantity) {
        return Inventory.builder()
                .id(UUID.randomUUID())
                .productId(productId)
                .quantity(quantity)
                .build();
    }

    public static Inventory fromPersistence(UUID id, UUID productId, Integer quantity, LocalDateTime lastUpdated) {
        try {
            return new Inventory(id, productId, quantity, lastUpdated);
        } catch (DomainException e) {
            throw new DomainException(ProductErrorCodes.INVENTORY_PERSISTENCE_ERROR, e);
        }
    }

    public void increaseStock(int amount) {
        if (amount <= 0) {
            throw new DomainException(ProductErrorCodes.INVENTORY_AMOUNT_NEGATIVE);
        }
        this.quantity += amount;
        touchStock();
    }

    public void decreaseStock(int amount) {
        if (amount <= 0) {
            throw new DomainException(ProductErrorCodes.INVENTORY_AMOUNT_NEGATIVE);
        }
        if (this.quantity < amount) {
            throw new DomainException(ProductErrorCodes.INVENTORY_QUANTITY_NEGATIVE, "Insufficient stock");
        }
        this.quantity -= amount;
        touchStock();
    }

    private void touchStock(){
        this.lastUpdated = LocalDateTime.now();
    }
}
