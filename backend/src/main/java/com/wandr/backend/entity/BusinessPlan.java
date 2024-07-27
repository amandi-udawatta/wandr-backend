package com.wandr.backend.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
public class BusinessPlan {
        private Long planId;
        private String name;
        private String description;
        private List<String> features;
        private BigDecimal price;

}
