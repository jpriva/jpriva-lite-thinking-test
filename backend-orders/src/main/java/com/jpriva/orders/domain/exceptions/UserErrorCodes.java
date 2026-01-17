package com.jpriva.orders.domain.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCodes implements ErrorCode {

    USER_PERSISTENCE_ERROR("USER_000", "User persistence error", 500),
    USER_ID_NULL("USER_001", "User id cannot be empty", 400),
    USER_EMAIL_NULL("USER_002", "User email cannot be empty", 400),
    USER_NAME_NULL("USER_003", "User name cannot be empty", 400),
    USER_ROLE_NULL("USER_004", "User role cannot be null", 400),
    USER_ALREADY_EXISTS("USER_005", "User already exists", 400),
    USER_NOT_FOUND("USER_006", "User not found", 404),
    USER_PASSWORD_NULL("USER_007", "User password cannot be empty", 400),
    USER_CREDENTIALS_INVALID("USER_008", "Invalid credentials", 401);

    private final String code;
    private final String message;
    private final int httpStatus;
}
