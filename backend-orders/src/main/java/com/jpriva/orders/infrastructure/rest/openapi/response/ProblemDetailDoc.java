package com.jpriva.orders.infrastructure.rest.openapi.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

@Schema(description = "Error response")
@Getter
@Setter
public class ProblemDetailDoc {

    @Schema(description = "Short description of the error", example = "Validation Error")
    private String title;

    @Schema(description = "HTTP Status code", example = "400")
    private int status;

    @Schema(description = "Error description", example = "Validation failed for the request.")
    private String detail;

    @Schema(description = "URI where the error occurred", example = "/api/orders/123")
    private URI instance;

    @Schema(
            description = "Business error code",
            example = "ORDER_LIMIT_EXCEEDED",
            nullable = true
    )
    private String errorCode;

    @Schema(
            description = "Field validation errors",
            example = "{\"price\": \"must be greater than 0\", \"sku\": \"cannot be empty\"}",
            nullable = true
    )
    private Map<String, String> errors;

    @Schema(
            description = "Timestamp of the error",
            example = "2023-09-20T15:30:00Z",
            nullable = true
    )
    private Instant timestamp;
}
