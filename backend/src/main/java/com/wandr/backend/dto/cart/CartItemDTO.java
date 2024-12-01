package com.wandr.backend.dto.cart;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class CartItemDTO {
    private Long travellerId;
    private Long productId;
    private Integer quantity;
    private String productName;   // For displaying product details
    private BigDecimal unitPrice; // Price per unit
    private BigDecimal totalPrice; // Total price (unitPrice * quantity)
}


