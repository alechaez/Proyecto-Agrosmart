package com.agrosmart.service;

import com.agrosmart.domain.Product;
import com.agrosmart.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final ProductRepository productRepository;

    @Transactional
    public Product adjustStock(Long productId, Integer delta, String reason) {
        if (delta == null) {
            throw new IllegalArgumentException("El campo 'delta' es obligatorio para ajustar stock");
        }
        if (delta == 0) {
            throw new IllegalArgumentException("Cantidad debe ser distinta de 0");
        }

        var p = productRepository.findById(productId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Producto no existe"));

        int actual = (p.getCurrentStock() == null ? 0 : p.getCurrentStock());

        if (delta < 0 && actual + delta < 0) {
            throw new IllegalArgumentException("Stock insuficiente");
        }

        int nuevo = actual + delta;
        p.setCurrentStock(nuevo);
        return productRepository.save(p);
    }
}

