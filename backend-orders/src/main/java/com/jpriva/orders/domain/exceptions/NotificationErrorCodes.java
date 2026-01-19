package com.jpriva.orders.domain.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCodes implements ErrorCode {
    NOTIFICATION_ERROR_SERIALIZATION("NOTIFICATION_001", "Error serializing message payload", 500);

    private final String code;
    private final String message;
    private final int httpStatus;
}
