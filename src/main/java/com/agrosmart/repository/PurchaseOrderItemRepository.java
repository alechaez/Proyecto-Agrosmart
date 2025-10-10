package com.agrosmart.repository;

import com.agrosmart.domain.PurchaseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Long> {}
