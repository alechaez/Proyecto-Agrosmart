package com.agrosmart.domain;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Producto del catálogo.
 * - sku: identificador único
 * - reorderPoint: punto de reorden (alerta de bajo stock)
 * - currentStock: stock actual (se incrementa al recibir OCs)
 */
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@Entity
@Table(
        name = "products",
        indexes = {
                @Index(name = "ix_products_name", columnList = "name"),
                @Index(name = "ix_products_sku", columnList = "sku", unique = true)
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ---- Datos básicos ----
    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 40, unique = true)
    private String sku;

    @Column(name = "unit", length = 20)        // ej: "kg", "lt", "und"
    private String unit;

    // ---- Inventario ----
    @Column(name = "reorder_point", nullable = false)
    @Builder.Default
    private Integer reorderPoint = 0;

    @Column(name = "current_stock", nullable = false)
    @Builder.Default
    private Integer currentStock = 0;

    // ---- (Opcional) Categoría ----
    // Quita estos campos si no usas Category.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    // ---- Helpers ----
    /** Ajusta el stock (puede ser negativo). */
    public void adjustStock(int delta) {
        this.currentStock = (this.currentStock == null ? 0 : this.currentStock) + delta;
    }


}
