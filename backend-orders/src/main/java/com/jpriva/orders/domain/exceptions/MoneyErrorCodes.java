package com.jpriva.orders.domain.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MoneyErrorCodes implements ErrorCode {

    MONEY_ERROR_AMOUNT("MONEY_001", "Amount cannot be empty", 400),
    MONEY_ERROR_CURRENCY("MONEY_002", "Currency cannot be empty", 400),
    MONEY_ERROR_CURRENCY_NOT_SUPPORTED("MONEY_003", "Currency not supported", 400);

    private final String code;
    private final String message;
    private final int httpStatus;
}
