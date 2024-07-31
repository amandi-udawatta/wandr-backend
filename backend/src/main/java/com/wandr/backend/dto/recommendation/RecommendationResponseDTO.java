package com.wandr.backend.dto.recommendation;


import lombok.Data;

@Data
public class RecommendationResponseDTO {
    private Long placeId;
    private String placeName;
    private Double similarity;

}


