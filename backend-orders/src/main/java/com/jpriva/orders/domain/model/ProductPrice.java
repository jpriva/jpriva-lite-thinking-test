package com.jpriva.orders.domain.model;

import com.jpriva.orders.domain.exceptions.DomainException;
import com.jpriva.orders.domain.exceptions.ProductErrorCodes;
import com.jpriva.orders.domain.model.vo.Money;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class ProductPrice {
    private final UUID id;
    private final UUID productId;
    private Money price;

    public ProductPrice(UUID id, UUID productId, Money price){
        if (id == null) {
            throw new DomainException(ProductErrorCodes.PRODUCT_PRICE_ID_NULL);
        }
        if (productId == null) {
            throw new DomainException(ProductErrorCodes.PRODUCT_ID_NULL);
        }
        if (price == null) {
            throw new DomainException(ProductErrorCodes.PRODUCT_PRICE_NULL);
        }

        this.id = id;
        this.productId = productId;
        this.price = price;
    }

    public static ProductPrice create(UUID productId, Money price){
        return new ProductPrice(UUID.randomUUID(), productId, price);
    }

    public static ProductPrice fromPersistence(UUID id, UUID productId, Money price) {
        try {
            return new ProductPrice(id, productId, price);
        } catch (DomainException e) {
            throw new DomainException(ProductErrorCodes.PRODUCT_PRICE_PERSISTENCE_ERROR, e);
        }
    }

    public void changePrice(BigDecimal price){
        this.price = this.price.changeAmount(price);
    }
}