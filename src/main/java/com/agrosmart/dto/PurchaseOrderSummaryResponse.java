package com.agrosmart.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PurchaseOrderSummaryResponse(
        Long id,
        String status,
        LocalDate issueDate,
        LocalDate receiveDate,
        BigDecimal total,
        Long supplierId,
        String supplierName
) {}
