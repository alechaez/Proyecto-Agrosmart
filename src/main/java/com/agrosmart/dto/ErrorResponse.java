package com.agrosmart.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record ErrorResponse(
        OffsetDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        String method,
        List<FieldError> errors
) {
    public record FieldError(String field, String message) {}
}
