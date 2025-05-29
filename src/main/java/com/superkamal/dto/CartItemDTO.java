package com.superkamal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItemDTO {
    private String productName;
    private int quantity;
    private double totalPrice;
}
