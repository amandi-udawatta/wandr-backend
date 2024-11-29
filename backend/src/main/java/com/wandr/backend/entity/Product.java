package com.wandr.backend.entity;

import lombok.Data;
import java.sql.Timestamp;


@Data
public class Product {
    private Long product_id;
    private String name;
    private String description;
    private Integer quantity;
    private Double price;
    private Long business_id;
    private Integer sales_count;
    private String image;
    private Double reservation_payment;
}
