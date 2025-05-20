package com.superkamal.models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    private String barcode;
    private String name;
    private double price;
    private int quantity;

    // Getters and Setters
}
