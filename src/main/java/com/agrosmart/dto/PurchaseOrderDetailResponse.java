package com.agrosmart.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PurchaseOrderDetailResponse(
        Long id,
        String status,
        LocalDate issueDate,
        LocalDate receiveDate,
        BigDecimal total,
        Long supplierId,
        String supplierName,
        List<Item> items
) {
    public record Item(
            Long id,
            Long productId,
            String productName,
            String sku,
            Integer qty,
            BigDecimal unitPrice
    ) {}
}
