package com.agrosmart.domain;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 40, unique = true)
    private String sku;

    @Column(name = "unit", length = 20)
    private String unit;

    @Column(name = "reorder_point", nullable = false)
    @Builder.Default
    private Integer reorderPoint = 0;

    @Column(name = "current_stock", nullable = false)
    @Builder.Default
    private Integer currentStock = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    /** Ajusta el stock (puede ser negativo). */
    public void adjustStock(int delta) {
        this.currentStock = (this.currentStock == null ? 0 : this.currentStock) + delta;
    }


}
