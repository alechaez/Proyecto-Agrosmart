package com.agrosmart.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "suppliers", indexes = {
        @Index(name = "ux_suppliers_ruc", columnList = "ruc", unique = true)
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Supplier {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 11)
    private String ruc;

    @Column(name = "contact_name", length = 100)
    private String contactName;

    @Column(length = 30)
    private String phone;
}