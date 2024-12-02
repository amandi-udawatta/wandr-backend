package com.wandr.backend.dto.product;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class ProductDTO {
    private Long product_id;
    private String name;
    private String description;
    private Integer quantity;
    private BigDecimal price;
    private Long business_id;
    private Integer sales_count;
    private BigDecimal reservation_payment;
    private String image;
}


