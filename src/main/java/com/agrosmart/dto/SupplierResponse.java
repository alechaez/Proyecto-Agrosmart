package com.agrosmart.dto;

public record SupplierResponse(
        Long id,
        String name,
        String ruc,
        String contactName,
        String phone
) {}
