package com.agrosmart.repository;

import com.agrosmart.domain.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Page<Supplier> findByNameContainingIgnoreCaseOrRucContainingIgnoreCase(
            String name, String ruc, Pageable pageable);

    boolean existsByRuc(String ruc);
    boolean existsByRucAndIdNot(String ruc, Long id);
}