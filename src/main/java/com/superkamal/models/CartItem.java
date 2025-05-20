package com.superkamal.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "barcode", referencedColumnName = "barcode")
    private Product product;

    private int quantity;

    private double totalPrice;

    @Transient
    private boolean discountApplied;

    @Transient
    private double originalPrice;

    @Transient
    private String discountDescription;
}
