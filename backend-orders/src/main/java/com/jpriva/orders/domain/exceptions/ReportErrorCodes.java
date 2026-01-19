package com.jpriva.orders.domain.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportErrorCodes implements ErrorCode {
    REPORT_GENERATE_ERROR("REPORT_001","An error occurred while generating the report", 500);

    private final String code;
    private final String message;
    private final int httpStatus;
}
