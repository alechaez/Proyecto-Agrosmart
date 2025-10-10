package com.agrosmart.service;

import com.agrosmart.domain.Product;
import com.agrosmart.dto.ProductRequest;
import com.agrosmart.dto.ProductUpdateRequest;
import com.agrosmart.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repo;

    @Transactional
    public Product create(ProductRequest req) {
        if (req.name() == null || req.name().isBlank()) {
            throw new IllegalArgumentException("name es requerido");
        }
        if (req.sku() == null || req.sku().isBlank()) {
            throw new IllegalArgumentException("sku es requerido");
        }
        if (req.reorderPoint() == null) {
            throw new IllegalArgumentException("reorderPoint es requerido");
        }

        final String name = req.name().trim();
        final String sku  = req.sku().trim();
        final String unit = (req.unit() == null ? null : req.unit().trim());

        if (repo.existsBySku(sku)) {
            throw new IllegalArgumentException("SKU ya existe");
        }

        var p = Product.builder()
                .name(name)
                .sku(sku)
                .unit(unit)
                .reorderPoint(req.reorderPoint())
                .currentStock(0)
                .build();

        return repo.save(p);
    }

    @Transactional
    public Product update(Long id, ProductUpdateRequest req) {
        var p = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

        if (req.name() != null && !req.name().isBlank()) {
            p.setName(req.name().trim());
        }
        if (req.sku() != null && !req.sku().isBlank()) {
            String nuevoSku = req.sku().trim();
            if (repo.existsBySkuAndIdNot(nuevoSku, id)) {
                throw new IllegalArgumentException("SKU ya existe en otro producto");
            }
            p.setSku(nuevoSku);
        }
        if (req.unit() != null && !req.unit().isBlank()) {
            p.setUnit(req.unit().trim());
        }
        if (req.reorderPoint() != null) {
            p.setReorderPoint(req.reorderPoint());
        }

        return repo.save(p);
    }
}
