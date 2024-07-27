package com.wandr.backend.dto.traveller;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TouristPlanDTO {
    private Long planId;
    private String name;
    private String description;
    private List<String> features;
    private BigDecimal price;
}
