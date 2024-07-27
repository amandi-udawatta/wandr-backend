package com.wandr.backend.dto.business;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class NewBusinessPlanDTO {
    private String name;
    private String description;
    private List<String> features;
    private BigDecimal price;
}
