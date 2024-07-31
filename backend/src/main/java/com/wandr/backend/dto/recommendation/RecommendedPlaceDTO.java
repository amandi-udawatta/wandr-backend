package com.wandr.backend.dto.recommendation;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RecommendedPlaceDTO {
    private Long id;
    private String name;
    private String description;
    private Double latitude;
    private Double longitude;
    private String address;
    private String image;
    private List<String> categories;
    private List<String> activities;
    private boolean liked;
    private BigDecimal similarity;
}


