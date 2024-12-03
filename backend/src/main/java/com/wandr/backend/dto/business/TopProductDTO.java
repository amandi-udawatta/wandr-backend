package com.wandr.backend.dto.business;

import lombok.Data;

@Data
public class TopProductDTO {
    private String productImage;
    private String productName;
    private int salesCount;
    private double totalRevenue;
}
