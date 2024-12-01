package com.wandr.backend.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;


@Data
public class Product {
    private Long product_id;
    private String name;
    private String description;
    private Integer quantity;
    private BigDecimal price;
    private Long business_id;
    private Integer sales_count;
    private String image;
    private BigDecimal reservation_payment;
}
