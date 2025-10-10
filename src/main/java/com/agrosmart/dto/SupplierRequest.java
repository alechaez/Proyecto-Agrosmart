package com.agrosmart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SupplierRequest(
        @NotBlank(message = "name es requerido")
        @Size(max = 120, message = "name máximo 120")
        String name,

        @NotBlank(message = "ruc es requerido")
        @Pattern(regexp = "\\d{11}", message = "ruc debe tener 11 dígitos")
        String ruc,

        @Size(max = 100, message = "contactName máximo 100")
        String contactName,

        @Size(max = 30, message = "phone máximo 30")
        String phone
) {}
