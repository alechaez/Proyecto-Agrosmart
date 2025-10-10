package com.agrosmart.repository;

import com.agrosmart.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List; // <-- IMPORT NECESARIO

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(
            String name, String sku, Pageable pageable
    );

    boolean existsBySku(String sku);
    boolean existsBySkuAndIdNot(String sku, Long id);

    @Query("select p from Product p where p.currentStock <= p.reorderPoint")
    List<Product> findLowStock();
}
