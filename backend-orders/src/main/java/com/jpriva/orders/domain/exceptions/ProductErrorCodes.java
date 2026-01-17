package com.jpriva.orders.domain.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCodes implements ErrorCode {

    PRODUCT_ID_NULL("PROD_001", "Product id cannot be empty", 400),
    PRODUCT_SKU_NULL("PROD_002", "Product sku cannot be empty", 400),
    PRODUCT_NAME_NULL("PROD_003", "Product name cannot be empty", 400),
    PRODUCT_COMPANY_ID_NULL("PROD_004", "Product company id cannot be empty", 400),
    PRODUCT_PERSISTENCE_ERROR("PROD_005", "Error persisting product", 500),
    PRODUCT_NOT_FOUND("PROD_006", "Product not found", 404),
    PRODUCT_INVENTORY_NULL("PROD_007", "Product inventory cannot be empty", 400),

    PRODUCT_PRICE_ID_NULL("PROD_PRICE_001", "Product price id cannot be empty", 400),
    PRODUCT_PRICE_NULL("PROD_PRICE_002", "Product price cannot be empty", 400),
    PRODUCT_PRICE_PERSISTENCE_ERROR("PROD_PRICE_003", "Error persisting product price", 500),
    PRODUCT_PRICE_NOT_FOUND("PROD_PRICE_004", "Product price not found", 404),

    INVENTORY_ID_NULL("INV_001", "Inventory id cannot be empty", 400),
    INVENTORY_QUANTITY_NEGATIVE("INV_002", "Inventory quantity cannot be negative", 400),
    INVENTORY_PERSISTENCE_ERROR("INV_003", "Error persisting inventory", 500),
    INVENTORY_AMOUNT_NEGATIVE("INV_004", "Amount must be positive", 400),
    INVENTORY_NOT_ENOUGH("INV_005", "Not enough units in the inventory", 400);

    private final String code;
    private final String message;
    private final int httpStatus;
}
