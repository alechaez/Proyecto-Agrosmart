package com.agrosmart.controller;

import com.agrosmart.domain.Product;
import com.agrosmart.dto.ProductResponse;
import com.agrosmart.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertsController {

    private final ProductRepository productRepository;

    @GetMapping("/low-stock")
    public List<ProductResponse> lowStock() {
        List<Product> list = productRepository.findLowStock();
        return list.stream()
                .map(p -> new ProductResponse(
                        p.getId(), p.getName(), p.getSku(), p.getUnit(),
                        p.getReorderPoint(), p.getCurrentStock()
                ))
                .toList();
    }
}

