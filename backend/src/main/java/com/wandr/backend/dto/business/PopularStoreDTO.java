package com.wandr.backend.dto.business;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PopularStoreDTO {
    private String shopName;
    private int salesCount;
    private double popularity;

}
