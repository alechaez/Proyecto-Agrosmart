package com.agrosmart.controller;

import com.agrosmart.domain.Product;
import com.agrosmart.dto.ProductRequest;
import com.agrosmart.dto.ProductUpdateRequest;
import com.agrosmart.dto.ProductResponse;
import com.agrosmart.repository.ProductRepository;
import com.agrosmart.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductService productService;

    @GetMapping
    public Page<ProductResponse> list(
            @RequestParam(required = false) String q,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        var page = (q != null && !q.isBlank())
                ? productRepository.findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(q, q, pageable)
                : productRepository.findAll(pageable);

        return page.map(this::toResponse);
    }

    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable Long id) {
        var p = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no existe"));
        return toResponse(p);
    }

    @PostMapping(produces = "text/plain")
    public ResponseEntity<String> create(@RequestBody @Validated ProductRequest req) {
        var p = productService.create(req);
        return ResponseEntity.status(201).body("Producto creado (id: " + p.getId() + ")");
    }

    @PutMapping(value = "/{id}", produces = "text/plain")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody ProductUpdateRequest req) {
        productService.update(id, req);
        return ResponseEntity.ok("Producto actualizado");
    }

    @DeleteMapping(value = "/{id}", produces = "text/plain")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Producto no existe");
        }
        productRepository.deleteById(id);
        return ResponseEntity.ok("Producto eliminado");
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getSku(),
                p.getUnit(),
                p.getReorderPoint(),
                p.getCurrentStock()
        );
    }
}
