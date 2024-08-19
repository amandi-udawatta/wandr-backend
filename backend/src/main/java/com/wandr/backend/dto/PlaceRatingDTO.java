package com.wandr.backend.dto;

import lombok.Data;

@Data
public class PlaceRatingDTO {
    private Long travellerId;
    private Long placeId;
    private Integer rating;
}
