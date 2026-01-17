package com.jpriva.orders.domain.model;

import com.jpriva.orders.domain.exceptions.DomainException;
import com.jpriva.orders.domain.exceptions.OrderErrorCodes;
import com.jpriva.orders.domain.model.vo.Money;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
public class OrderItem {

    private final UUID id;
    private final UUID orderId;
    private final UUID productId;
    private final String productName;
    private int quantity;
    private Money unitPrice;

    @Builder
    public OrderItem(UUID id, UUID orderId, UUID productId, String productName, int quantity, Money unitPrice){
        if (id == null){
            throw new DomainException(OrderErrorCodes.ORDER_ITEM_ID_NULL);
        }
        if (orderId == null){
            throw new DomainException(OrderErrorCodes.ORDER_ID_NULL);
        }
        if (productId == null || productName == null){
            throw new DomainException(OrderErrorCodes.ORDER_PRODUCT_NULL);
        }
        if (quantity <= 0){
            throw new DomainException(OrderErrorCodes.ORDER_PRODUCT_QUANTITY_NULL);
        }
        if (unitPrice == null){
            throw new DomainException(OrderErrorCodes.ORDER_PRODUCT_PRICE_NULL);
        }

        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public static OrderItem create(UUID orderId, Product product, int quantity, ProductPrice productPrice) {
        if (product == null) {
            throw new DomainException(OrderErrorCodes.ORDER_PRODUCT_NULL);
        }
        if (productPrice == null) {
            throw new DomainException(OrderErrorCodes.ORDER_PRODUCT_PRICE_NULL);
        }
        if (quantity <= 0){
            throw new DomainException(OrderErrorCodes.ORDER_PRODUCT_QUANTITY_NULL);
        }
        return OrderItem.builder()
                .id(UUID.randomUUID())
                .orderId(orderId)
                .productId(product.getId())
                .productName(product.getName())
                .quantity(quantity)
                .unitPrice(productPrice.getPrice())
                .build();
    }

    public static OrderItem fromPersistence(UUID id, UUID orderId, UUID productId, String productName, int quantity, Money unitPrice) {
        try {
            return OrderItem.builder()
                    .id(id)
                    .orderId(orderId)
                    .productId(productId)
                    .productName(productName)
                    .quantity(quantity)
                    .unitPrice(unitPrice)
                    .build();
        } catch (DomainException e) {
            throw new DomainException(OrderErrorCodes.ORDER_ITEM_PERSISTENCE_ERROR, e);
        }
    }

    public void changeQuantity(int quantity) {
        if (quantity <= 0) {
            throw new DomainException(OrderErrorCodes.ORDER_PRODUCT_QUANTITY_NULL);
        }
        this.quantity = quantity;
    }

    public void changeUnitPrice(Money unitPrice) {
        if (unitPrice == null) {
            throw new DomainException(OrderErrorCodes.ORDER_PRODUCT_PRICE_NULL);
        }
        this.unitPrice = unitPrice;
    }

}