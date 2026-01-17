package com.jpriva.orders.domain.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompanyErrorCodes implements ErrorCode {
    COMPANY_ID_NULL("COMP_001", "Company id cannot be empty", 400),
    COMPANY_NAME_NULL("COMP_002", "Company name cannot be empty", 400),
    COMPANY_TAX_ID_NULL("COMP_003", "Company tax id cannot be empty", 400),
    COMPANY_PERSISTENCE_ERROR("COMP_004", "Error persisting company", 500),
    COMPANY_ALREADY_EXISTS("COMP_005", "Company already exists", 400),
    COMPANY_NOT_FOUND("COMP_006", "Company not found", 404);

    private final String code;
    private final String message;
    private final int httpStatus;
}
