package com.wandr.backend.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TouristPlan {
        private Long planId;
        private String name;
        private String description;
        private List<String> features;
        private BigDecimal price;

}
