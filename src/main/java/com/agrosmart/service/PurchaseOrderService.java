package com.agrosmart.service;

import com.agrosmart.domain.Product;
import com.agrosmart.domain.PurchaseOrder;
import com.agrosmart.domain.PurchaseOrderItem;
import com.agrosmart.domain.Supplier;
import com.agrosmart.dto.PurchaseOrderAddItemRequest;
import com.agrosmart.dto.PurchaseOrderCreateRequest;
import com.agrosmart.repository.ProductRepository;
import com.agrosmart.repository.PurchaseOrderItemRepository;
import com.agrosmart.repository.PurchaseOrderRepository;
import com.agrosmart.repository.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository poRepo;
    private final PurchaseOrderItemRepository itemRepo;
    private final SupplierRepository supplierRepo;
    private final ProductRepository productRepo;

    @Transactional
    public PurchaseOrder create(PurchaseOrderCreateRequest req) {
        Supplier supplier = supplierRepo.findById(req.supplierId())
                .orElseThrow(() -> new EntityNotFoundException("Proveedor no existe"));

        PurchaseOrder po = PurchaseOrder.builder()
                .supplier(supplier)
                .status("DRAFT")
                .issueDate(LocalDate.now())
                .total(BigDecimal.ZERO)
                .build();

        return poRepo.save(po);
    }

    @Transactional
    public PurchaseOrder addItem(Long poId, PurchaseOrderAddItemRequest req) {
        PurchaseOrder po = poRepo.findById(poId)
                .orElseThrow(() -> new EntityNotFoundException("PO no existe"));

        if (!"DRAFT".equals(po.getStatus())) {
            throw new IllegalStateException("No se pueden agregar ítems cuando la OC no está en DRAFT");
        }

        if (req.qty() == null || req.qty() <= 0) {
            throw new IllegalArgumentException("Cantidad debe ser > 0");
        }
        if (req.unitPrice() == null || req.unitPrice().signum() <= 0) {
            throw new IllegalArgumentException("Precio debe ser > 0");
        }

        Product product = productRepo.findById(req.productId())
                .orElseThrow(() -> new EntityNotFoundException("Producto no existe"));

        PurchaseOrderItem item = PurchaseOrderItem.builder()
                .purchaseOrder(po)
                .product(product)
                .qty(req.qty())
                .unitPrice(req.unitPrice())
                .build();

        itemRepo.save(item);
        po.getItems().add(item);

        recalcTotal(po);
        return poRepo.save(po);
    }

    @Transactional
    public PurchaseOrder send(Long poId) {
        PurchaseOrder po = poRepo.findById(poId)
                .orElseThrow(() -> new EntityNotFoundException("PO no existe"));

        if (!"DRAFT".equals(po.getStatus())) {
            throw new IllegalStateException("Solo se puede enviar una OC en estado DRAFT");
        }
        if (po.getItems().isEmpty()) {
            throw new IllegalStateException("No se puede enviar una OC sin ítems");
        }

        po.setStatus("SENT");
        return poRepo.save(po);
    }

    @Transactional
    public PurchaseOrder receive(Long poId) {
        PurchaseOrder po = poRepo.findById(poId)
                .orElseThrow(() -> new EntityNotFoundException("PO no existe"));

        if (!"SENT".equals(po.getStatus())) {
            throw new IllegalStateException("Solo se puede recibir una OC en estado SENT");
        }

        po.getItems().forEach(i -> {
            Product p = i.getProduct();
            int actual = (p.getCurrentStock() == null ? 0 : p.getCurrentStock());
            p.setCurrentStock(actual + i.getQty());
            productRepo.save(p);
        });

        po.setStatus("RECEIVED");
        po.setReceiveDate(LocalDate.now());
        return poRepo.save(po);
    }

    /** NUEVO: Eliminar OC (solo DRAFT) */
    @Transactional
    public void delete(Long id) {
        var po = poRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OC no existe"));

        if (!"DRAFT".equalsIgnoreCase(po.getStatus())) {
            throw new IllegalStateException("Solo se puede eliminar una OC en estado DRAFT");
        }

        if (po.getItems() != null) {
            po.getItems().clear(); // con orphanRemoval se borran en BD
        }
        poRepo.delete(po);
    }

    /** NUEVO: Eliminar ítem de OC (solo DRAFT) */
    @Transactional
    public PurchaseOrder removeItem(Long poId, Long itemId) {
        var po = poRepo.findById(poId)
                .orElseThrow(() -> new EntityNotFoundException("OC no existe"));

        if (!"DRAFT".equalsIgnoreCase(po.getStatus())) {
            throw new IllegalStateException("Solo se pueden borrar ítems en una OC DRAFT");
        }

        var item = itemRepo.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Ítem no existe"));

        if (item.getPurchaseOrder() == null || !item.getPurchaseOrder().getId().equals(poId)) {
            throw new IllegalArgumentException("El ítem no pertenece a la OC indicada");
        }

        po.getItems().remove(item);
        item.setPurchaseOrder(null); // defensivo

        recalcTotal(po);
        return poRepo.save(po);
    }

    /* ====================== Helpers ====================== */

    private void recalcTotal(PurchaseOrder po) {
        BigDecimal total = po.getItems().stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQty())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        po.setTotal(total.setScale(2, RoundingMode.HALF_UP));
    }
}
