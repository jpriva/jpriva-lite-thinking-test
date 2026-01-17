package com.jpriva.orders.domain.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UUIDErrorCodes implements ErrorCode {

    INVALID_UUID_FORMAT("UUID_001", "Invalid UUID format", 400);

    private final String code;
    private final String message;
    private final int httpStatus;
}
