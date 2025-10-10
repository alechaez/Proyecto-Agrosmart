package com.agrosmart.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public record PurchaseOrderUpdateRequest(
        Long supplierId,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate issueDate
) {}
