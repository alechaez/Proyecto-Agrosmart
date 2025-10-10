package com.agrosmart.dto;

public record ProductUpdateRequest(
        String name,
        String sku,
        String unit,
        Integer reorderPoint
) {}
