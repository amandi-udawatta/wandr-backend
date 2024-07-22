package com.wandr.backend.entity;

import lombok.Data;
import java.sql.Timestamp;


@Data
public class Cart {
    private Long cartId;
    private Long travellerId;
    private Timestamp createdAt;
}
