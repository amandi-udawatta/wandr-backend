package com.wandr.backend.dto.product;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class UpdateProductDTO {
    private String name;
    private String description;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal reservation_payment;
    private String image;
}


