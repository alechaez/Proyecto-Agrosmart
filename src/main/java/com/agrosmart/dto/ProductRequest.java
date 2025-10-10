package com.agrosmart.dto;

import jakarta.validation.constraints.*;

public record ProductRequest(
        @NotBlank(message = "name es requerido")
        @Size(max = 150, message = "name máximo 150")
        String name,

        @NotBlank(message = "sku es requerido")
        @Size(max = 40, message = "sku máximo 40")
        String sku,

        @Size(max = 15, message = "unit máximo 15")
        String unit,

        @NotNull(message = "reorderPoint es requerido")
        @Min(value = 0, message = "reorderPoint >= 0")
        Integer reorderPoint
) {}
