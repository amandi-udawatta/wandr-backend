package com.wandr.backend.dto.recommendation;

import lombok.Data;

import java.util.List;

@Data
public class RecommendationRequestDTO {
    private UserDTO user;
    private List<PlacesDTO> places;
}


