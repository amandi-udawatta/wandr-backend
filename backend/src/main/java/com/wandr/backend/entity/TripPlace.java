package com.wandr.backend.entity;

import lombok.Data;

@Data
public class TripPlace {
    private Long tripPlaceId;
    private Long tripId;
    private Long placeId;
    private String title;
    private String description;
    private Integer placeOrder;
    private Integer optimizedOrder;
    private Boolean visited;
    private String imageName;
    private Integer rating;
}

