package com.jpriva.orders.domain.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCodes implements ErrorCode {

    ORDER_ID_NULL("ORDER_000", "Order id cannot be empty", 400),
    ORDER_COMPANY_ID_NULL("ORDER_001", "Company id cannot be empty", 400),
    ORDER_CLIENT_ID_NULL("ORDER_002", "Client id cannot be empty", 400),
    ORDER_CLIENT_NAME_NULL("ORDER_003", "Client name cannot be empty", 400),
    ORDER_ADDRESS_NULL("ORDER_004", "Address cannot be empty", 400),
    ORDER_STATUS_NULL("ORDER_005", "Status cannot be empty", 400),
    ORDER_PERSISTENCE_ERROR("ORDER_006", "Error persisting order", 500),
    ORDER_TOTAL_AMOUNT_NULL("ORDER_007", "Total amount cannot be empty", 400),
    ORDER_NO_ITEM_ADDED("ORDER_008", "No item added to order", 400),
    ORDER_NO_ITEM_REMOVED("ORDER_009", "No item added to order", 400),
    ORDER_DATE_NULL("ORDER_010", "Order date cannot be empty", 400),
    ORDER_ITEM_ALREADY_ADDED("ORDER_011", "Item is already added to the order", 400),
    ORDER_ITEM_CURRENCY_MISMATCH("ORDER_012", "Item currency mismatch", 400),
    ORDER_ITEM_NOT_FOUND("ORDER_013", "Item not found", 400),
    ORDER_NOT_FOUND("ORDER_014", "Order not found", 404),
    ORDER_STATUS_NOT_PENDING("ORDER_015", "Order status is not pending", 400),
    ORDER_ALREADY_SHIPPED("ORDER_016", "Order has already been shipped", 400),
    ORDER_ALREADY_DELIVERED("ORDER_017", "Order has already been delivered", 400),
    ORDER_STATUS_NOT_CONFIRMED("ORDER_018", "Order status is not confirmed", 400),
    ORDER_NOT_ALLOWED("ORDER_019", "Order not allowed", 403),

    ORDER_ITEM_ID_NULL("ORDER_ITEM_001", "Order detail id cannot be empty", 400),
    ORDER_PRODUCT_NULL("ORDER_ITEM002", "Product cannot be empty", 400),
    ORDER_PRODUCT_PRICE_NULL("ORDER_ITEM_003", "Product Price cannot be empty", 400),
    ORDER_PRODUCT_QUANTITY_NULL("ORDER_ITEM_004", "Product quantity cannot be empty", 400),
    ORDER_ITEM_PERSISTENCE_ERROR("ORDER_ITEM_005", "Error persisting order item", 500);

    private final String code;
    private final String message;
    private final int httpStatus;
}
