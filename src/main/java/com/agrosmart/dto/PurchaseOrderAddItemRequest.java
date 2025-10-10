package com.agrosmart.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PurchaseOrderAddItemRequest(
        @NotNull Long productId,
        @NotNull Integer qty,
        @NotNull BigDecimal unitPrice
) {}
