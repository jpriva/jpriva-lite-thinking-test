package com.jpriva.orders.infrastructure.persistence.mapper;

import com.jpriva.orders.domain.model.Product;
import com.jpriva.orders.domain.model.ProductPrice;
import com.jpriva.orders.domain.model.Inventory;
import com.jpriva.orders.domain.model.vo.Money;
import com.jpriva.orders.infrastructure.persistence.entity.ProductEntity;
import com.jpriva.orders.infrastructure.persistence.entity.ProductPriceEntity;
import com.jpriva.orders.infrastructure.persistence.entity.InventoryEntity;

import java.util.Map;
import java.util.stream.Collectors;

public class ProductMapper {

    private ProductMapper() {}

    public static Product toDomain(ProductEntity entity) {
        if (entity == null) return null;

        Map<String, ProductPrice> prices = entity.getPrices().stream()
                .collect(Collectors.toMap(
                        ProductPriceEntity::getCurrencyCode,
                        p -> ProductPrice.fromPersistence(
                                p.getId(),
                                p.getProduct().getId(),
                                Money.fromString(p.getCurrencyCode(), p.getPrice().toString())
                        )
                ));

        Inventory inventory = null;
        if (entity.getInventory() != null) {
            inventory = Inventory.fromPersistence(
                    entity.getInventory().getId(),
                    entity.getInventory().getProduct().getId(),
                    entity.getInventory().getQuantity(),
                    entity.getInventory().getLastUpdated()
            );
        }

        return Product.fromPersistence(
                entity.getId(),
                entity.getCompanyId(),
                entity.getCategoryId(),
                entity.getName(),
                entity.getSku(),
                entity.getDescription(),
                entity.getCreatedAt(),
                inventory,
                prices
        );
    }

    public static ProductEntity toEntity(Product domain) {
        if (domain == null) return null;
        
        ProductEntity entity = ProductEntity.builder()
                .id(domain.getId())
                .companyId(domain.getCompanyId())
                .categoryId(domain.getCategoryId())
                .name(domain.getName())
                .sku(domain.getSku())
                .description(domain.getDescription())
                .createdAt(domain.getCreatedAt())
                .build();
        
        if (domain.getInventory() != null) {
            InventoryEntity invEntity = InventoryEntity.builder()
                    .id(domain.getInventory().getId())
                    .product(entity)
                    .quantity(domain.getInventory().getQuantity())
                    .lastUpdated(domain.getInventory().getLastUpdated())
                    .build();
            entity.setInventory(invEntity);
        }
        
        if (domain.getPrices() != null) {
            entity.setPrices(domain.getPrices().values().stream()
                    .map(p -> ProductPriceEntity.builder()
                            .id(p.getId())
                            .product(entity)
                            .currencyCode(p.getPrice().currency().getCode())
                            .price(p.getPrice().amount())
                            .build())
                    .collect(Collectors.toList()));
        }

        return entity;
    }
}
