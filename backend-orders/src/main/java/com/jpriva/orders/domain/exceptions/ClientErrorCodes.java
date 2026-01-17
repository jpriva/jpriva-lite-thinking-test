package com.jpriva.orders.domain.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClientErrorCodes implements ErrorCode {
    CLIENT_ID_NULL("CLI_001", "Client id cannot be empty", 400),
    CLIENT_COMPANY_ID_NULL("CLI_002", "Client company id cannot be empty", 400),
    CLIENT_NAME_NULL("CLI_003", "Client name cannot be empty", 400),
    CLIENT_USER_ID_NULL("CLI_004", "Client user id cannot be empty", 400),
    CLIENT_PERSISTENCE_ERROR("CLI_005", "Error persisting client", 500),
    CLIENT_NOT_FOUND("CLI_006", "Client not found", 404);

    private final String code;
    private final String message;
    private final int httpStatus;
}
