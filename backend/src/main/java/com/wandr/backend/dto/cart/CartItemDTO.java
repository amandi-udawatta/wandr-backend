package com.wandr.backend.dto.cart;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CartItemDTO {
    private long cartItemId;
    private long cartId;
    private long productId;
    private long unitId;
    private Timestamp addedAt;
    private Integer quantity;
}


