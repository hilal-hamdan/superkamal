package com.superkamal.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "discounts")
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String barcode;

    @Enumerated(EnumType.STRING)
    private DiscountType type;

    private Double newUnitPrice;
    private Integer bundleQuantity;
    private Double bundlePrice;
}
