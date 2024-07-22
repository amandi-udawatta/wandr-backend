package com.wandr.backend.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CartItem {
    private long cartItemId;
    private long cartId;
    private long productId;
    private long unitId;
    private Timestamp addedAt;
    private Integer quantity;
}

