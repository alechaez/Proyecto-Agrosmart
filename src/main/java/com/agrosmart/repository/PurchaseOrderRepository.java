package com.agrosmart.repository;

import com.agrosmart.domain.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.time.LocalDate;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    Page<PurchaseOrder> findByStatus(String status, Pageable pageable);

    Page<PurchaseOrder> findBySupplier_Id(Long supplierId, Pageable pageable);

    Page<PurchaseOrder> findByIssueDateBetween(LocalDate from, LocalDate to, Pageable pageable);

    Page<PurchaseOrder> findByStatusAndIssueDateBetween(String status, LocalDate from, LocalDate to, Pageable pageable);

    boolean existsBySupplierId(Long supplierId);
}
