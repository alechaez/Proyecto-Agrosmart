package com.agrosmart.controller;

import com.agrosmart.dto.ProductResponse;
import com.agrosmart.service.InventoryService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
public class InventoryController {

    private final InventoryService service;

    @PostMapping(value = "/{id}/adjust-stock", produces = "text/plain")
    public ResponseEntity<String> adjust(
            @PathVariable Long id,
            @RequestBody @Validated StockAdjustRequest req
    ) {
        if (req.delta() == null || req.delta() == 0) {
            return ResponseEntity.badRequest().body("El valor ingresado debe ser distinto de 0");
        }

        var p = service.adjustStock(id, req.delta(), req.reason());

        var motivo = (req.reason() != null && !req.reason().isBlank())
                ? " Motivo: " + req.reason()
                : "";

        String msg = (req.delta() > 0)
                ? "Ingreso registrado (+" + req.delta() + "). Stock actual: " + p.getCurrentStock() + "." + motivo
                : "Salida registrada (" + req.delta() + "). Stock actual: " + p.getCurrentStock() + "." + motivo;

        return ResponseEntity.ok(msg);
    }

    public record StockAdjustRequest(
            @NotNull(message = "El campo es obligatorio para ajustar stock")
            Integer delta,
            String reason
    ) {}
}
