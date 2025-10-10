package com.agrosmart.dto;

import jakarta.validation.constraints.NotNull;

public record PurchaseOrderCreateRequest(@NotNull Long supplierId) {}
