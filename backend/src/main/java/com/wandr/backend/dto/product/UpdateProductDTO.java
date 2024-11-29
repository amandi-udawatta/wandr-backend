package com.wandr.backend.dto.product;

import lombok.Data;


@Data
public class UpdateProductDTO {
    private String name;
    private String description;
    private Integer quantity;
    private Double price;
    private Double reservation_payment;
//    private String image;
}


