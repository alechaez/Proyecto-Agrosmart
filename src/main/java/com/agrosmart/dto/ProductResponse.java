package com.agrosmart.dto;
public record ProductResponse(Long id, String name, String sku, String unit,
                              Integer reorderPoint, Integer currentStock) {}
