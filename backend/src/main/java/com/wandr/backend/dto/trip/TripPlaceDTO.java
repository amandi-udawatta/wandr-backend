package com.wandr.backend.dto.trip;

import lombok.Data;

@Data
public class TripPlaceDTO {
    private Long tripPlaceId;
    private Long placeId;
    private String title;
    private Double latitude;
    private Double longitude;
    private Integer placeOrder;
    private Integer optimizedOrder;
    private Integer rating;
}

//TODO: lat long daanna methanata