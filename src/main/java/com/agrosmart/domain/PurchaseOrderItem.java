package com.agrosmart.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "purchase_order_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PurchaseOrderItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "purchase_order_id")
    @JsonIgnore                 // <<< evita que se serialice el padre y corte la recursión
    private PurchaseOrder purchaseOrder;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer qty;

    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice;
}
