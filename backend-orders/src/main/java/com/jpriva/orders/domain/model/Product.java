package com.jpriva.orders.domain.model;

import com.jpriva.orders.domain.exceptions.DomainException;
import com.jpriva.orders.domain.exceptions.ProductErrorCodes;
import com.jpriva.orders.domain.model.vo.Currency;
import com.jpriva.orders.domain.model.vo.Money;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class Product {

    private final UUID id;
    private final UUID companyId;
    private UUID categoryId;
    private String name;
    private String sku;
    private String description;
    private final LocalDateTime createdAt;

    private final Inventory inventory;
    private final Map<String, ProductPrice> prices;

    @Builder
    public Product(UUID id, UUID companyId, UUID categoryId, String name, String sku, String description, LocalDateTime createdAt, Inventory inventory, Map<String, ProductPrice> prices) {
        if (id == null){
            throw new DomainException(ProductErrorCodes.PRODUCT_ID_NULL);
        }
        if (companyId == null){
            throw new DomainException(ProductErrorCodes.PRODUCT_COMPANY_ID_NULL);
        }
        if (prices == null){
            prices = new HashMap<>();
        }
        if (inventory == null){
            throw new DomainException(ProductErrorCodes.PRODUCT_INVENTORY_NULL);
        }
        this.id = id;
        this.companyId = companyId;
        changeCategory(categoryId);
        changeName(name);
        changeSku(sku);
        changeDescription(description);
        this.createdAt = createdAt;
        this.inventory = inventory;
        this.prices = prices;
    }

    public static Product create(UUID companyId, UUID categoryId, String name, String sku, String description) {
        UUID id = UUID.randomUUID();
        Inventory inventory = Inventory.create(id, 0);
        return Product.builder()
                .id(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .companyId(companyId)
                .categoryId(categoryId)
                .name(name)
                .sku(sku)
                .description(description)
                .inventory(inventory)
                .build();
    }

    public static Product fromPersistence(UUID id, UUID companyId, UUID categoryId, String name, String sku, String description, LocalDateTime createdAt, Inventory inventory, Map<String, ProductPrice> prices) {
        try {
            return Product.builder()
                    .id(id)
                    .companyId(companyId)
                    .categoryId(categoryId)
                    .name(name)
                    .sku(sku)
                    .description(description)
                    .createdAt(createdAt)
                    .inventory(inventory)
                    .prices(prices)
                    .build();
        } catch (DomainException e) {
            throw new DomainException(ProductErrorCodes.PRODUCT_PERSISTENCE_ERROR, e);
        }
    }

    public void changeName(String name) {
        if (name == null || name.isBlank()){
            throw new DomainException(ProductErrorCodes.PRODUCT_NAME_NULL);
        }
        this.name = name.trim();
    }

    public void changeSku(String sku) {
        if (sku == null || sku.isBlank()) {
            throw new DomainException(ProductErrorCodes.PRODUCT_SKU_NULL);
        }
        this.sku = sku.trim();
    }

    public void changeDescription(String description) {
        if (description == null || description.isBlank()) {
            this.description = null;
            return;
        }
        this.description = description.trim();
    }

    public void changeCategory(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public void increaseStock(int amount) {
        inventory.increaseStock(amount);
    }

    public void decreaseStock(int amount) {
        inventory.decreaseStock(amount);
    }

    public void changePrice(Money price){
        if (price == null){
            throw new DomainException(ProductErrorCodes.PRODUCT_PRICE_NULL);
        }
        ProductPrice newPrice;
        if (!prices.containsKey(price.currency().getCode())){
            newPrice = ProductPrice.create(this.id, price);
        } else {
            newPrice = prices.get(price.currency().getCode());
            newPrice.changePrice(price.amount());
        }
        prices.put(price.currency().getCode(), newPrice);
    }

    public ProductPrice getProductPrice(Currency currency){
        if (!prices.containsKey(currency.getCode())){
            throw new DomainException(ProductErrorCodes.PRODUCT_PRICE_NOT_FOUND);
        }
        return prices.get(currency.getCode());
    }

}