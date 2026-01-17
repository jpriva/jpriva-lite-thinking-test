package com.jpriva.orders.domain.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryErrorCodes implements ErrorCode {
    CATEGORY_ID_NULL("CAT_001", "Category id cannot be empty", 400),
    CATEGORY_COMPANY_ID_NULL("CAT_002", "Category company id cannot be empty", 400),
    CATEGORY_NAME_NULL("CAT_003", "Category name cannot be empty", 400),
    CATEGORY_PERSISTENCE_ERROR("CAT_004", "Error persisting category", 500);

    private final String code;
    private final String message;
    private final int httpStatus;
}
