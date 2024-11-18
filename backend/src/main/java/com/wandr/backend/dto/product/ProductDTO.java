package com.wandr.backend.dto.product;

import lombok.Data;


@Data
public class ProductDTO {
    private Long product_id;
    private String name;
    private String description;
    private Integer quantity;
    private Double price;
    private Long business_id;
    private Integer sales_count;
//    private String image;
}


