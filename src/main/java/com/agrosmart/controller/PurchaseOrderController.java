package com.agrosmart.controller;

import com.agrosmart.domain.PurchaseOrder;
import com.agrosmart.domain.PurchaseOrderItem;
import com.agrosmart.dto.PurchaseOrderAddItemRequest;
import com.agrosmart.dto.PurchaseOrderCreateRequest;
import com.agrosmart.dto.PurchaseOrderDetailResponse;
import com.agrosmart.dto.PurchaseOrderStatusResponse;
import com.agrosmart.dto.PurchaseOrderSummaryResponse;
import com.agrosmart.repository.PurchaseOrderRepository;
import com.agrosmart.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderRepository poRepo;
    private final PurchaseOrderService poService;

    @PostMapping(produces = "text/plain")
    public ResponseEntity<String> create(@RequestBody PurchaseOrderCreateRequest req) {
        PurchaseOrder po = poService.create(req);
        return ResponseEntity.status(201).body("OC creada (id: " + po.getId() + ")");
    }

    @PostMapping(value = "/{id}/items", produces = "text/plain")
    public ResponseEntity<String> addItem(@PathVariable Long id,
                                          @RequestBody PurchaseOrderAddItemRequest req) {
        poService.addItem(id, req);
        return ResponseEntity.ok("Ítem agregado a la OC");
    }

    @PostMapping(value = "/{id}/send", produces = "text/plain")
    public ResponseEntity<String> send(@PathVariable Long id) {
        poService.send(id);
        return ResponseEntity.ok("OC enviada");
    }

    @PostMapping(value = "/{id}/receive", produces = "text/plain")
    public ResponseEntity<String> receive(@PathVariable Long id) {
        poService.receive(id);
        return ResponseEntity.ok("OC recibida");
    }

    @GetMapping
    public Page<PurchaseOrderSummaryResponse> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<PurchaseOrder> page;

        if (status != null && dateFrom != null && dateTo != null) {
            page = poRepo.findByStatusAndIssueDateBetween(status, dateFrom, dateTo, pageable);
        } else if (status != null) {
            page = poRepo.findByStatus(status, pageable);
        } else if (supplierId != null) {
            page = poRepo.findBySupplier_Id(supplierId, pageable);
        } else if (dateFrom != null && dateTo != null) {
            page = poRepo.findByIssueDateBetween(dateFrom, dateTo, pageable);
        } else {
            page = poRepo.findAll(pageable);
        }

        return page.map(this::toSummary);
    }

    @GetMapping("/{id}")
    public PurchaseOrderDetailResponse getDetail(@PathVariable Long id) {
        var po = poRepo.findById(id).orElseThrow();

        po.getItems().size();
        if (po.getSupplier() != null) { po.getSupplier().getId(); }
        return toDetail(po);
    }
    @DeleteMapping(value = "/{id}", produces = "text/plain")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        poService.delete(id);
        return ResponseEntity.ok("OC eliminada correctamente");
    }

    @DeleteMapping("/{poId}/items/{itemId}")
    public PurchaseOrderDetailResponse removeItem(@PathVariable Long poId, @PathVariable Long itemId) {
        var po = poService.removeItem(poId, itemId);
        return toDetail(po);
    }

    private PurchaseOrderSummaryResponse toSummary(PurchaseOrder po) {
        return new PurchaseOrderSummaryResponse(
                po.getId(),
                po.getStatus(),
                po.getIssueDate(),
                po.getReceiveDate(),
                po.getTotal(),
                po.getSupplier() != null ? po.getSupplier().getId() : null,
                po.getSupplier() != null ? po.getSupplier().getName() : null
        );
    }

    private PurchaseOrderDetailResponse toDetail(PurchaseOrder po) {
        List<PurchaseOrderDetailResponse.Item> items = po.getItems().stream()
                .map(this::toItem)
                .toList();

        return new PurchaseOrderDetailResponse(
                po.getId(),
                po.getStatus(),
                po.getIssueDate(),
                po.getReceiveDate(),
                po.getTotal(),
                po.getSupplier() != null ? po.getSupplier().getId() : null,
                po.getSupplier() != null ? po.getSupplier().getName() : null,
                items
        );
    }

    private PurchaseOrderDetailResponse.Item toItem(PurchaseOrderItem i) {
        return new PurchaseOrderDetailResponse.Item(
                i.getId(),
                i.getProduct() != null ? i.getProduct().getId() : null,
                i.getProduct() != null ? i.getProduct().getName() : null,
                i.getProduct() != null ? i.getProduct().getSku() : null,
                i.getQty(),
                i.getUnitPrice()
        );
    }
}
