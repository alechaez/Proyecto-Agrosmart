package com.agrosmart.controller;

import com.agrosmart.domain.Supplier;
import com.agrosmart.dto.SupplierRequest;
import com.agrosmart.dto.SupplierResponse;
import com.agrosmart.repository.SupplierRepository;
import com.agrosmart.service.SupplierService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.agrosmart.repository.PurchaseOrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierRepository repo;
    private final SupplierService service;
    private final PurchaseOrderRepository poRepo;
    private final PurchaseOrderRepository purchaseOrderRepository;

    @GetMapping
    public Page<SupplierResponse> list(@RequestParam(required = false) String q,
                                       @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        var page = (q != null && !q.isBlank())
                ? repo.findByNameContainingIgnoreCaseOrRucContainingIgnoreCase(q, q, pageable)
                : repo.findAll(pageable);

        return page.map(this::toResponse);
    }

    @GetMapping("/{id}")
    public SupplierResponse getById(@PathVariable Long id) {
        var s = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Proveedor no existe"));
        return toResponse(s);
    }

    @PostMapping
    public SupplierResponse create(@RequestBody @Validated SupplierRequest req) {
        var s = service.create(req);
        return toResponse(s);
    }

    @PutMapping("/{id}")
    public SupplierResponse update(@PathVariable Long id, @RequestBody SupplierRequest req) {
        var s = service.update(id, req);
        return toResponse(s);
    }

    @DeleteMapping(value = "/{id}", produces = "text/plain")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("Proveedor no existe");
        }
        if (poRepo.existsBySupplierId(id)) {
            return ResponseEntity.status(409).body("No se puede eliminar: tiene órdenes de compra asociadas");
        }
        repo.deleteById(id);
        return ResponseEntity.ok("Proveedor eliminado correctamente");
    }

    private SupplierResponse toResponse(Supplier s) {
        return new SupplierResponse(s.getId(), s.getName(), s.getRuc(), s.getContactName(), s.getPhone());
    }
}
