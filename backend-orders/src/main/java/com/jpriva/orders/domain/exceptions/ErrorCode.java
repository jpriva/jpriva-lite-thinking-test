package com.jpriva.orders.domain.exceptions;

public interface ErrorCode {
    String getCode();

    String getMessage();

    int getHttpStatus();
}
