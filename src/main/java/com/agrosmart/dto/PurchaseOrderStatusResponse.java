package com.agrosmart.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PurchaseOrderStatusResponse(
        Long id,
        String status,
        LocalDate issueDate,
        LocalDate receiveDate,
        BigDecimal total
) {}
