package com.wandr.backend.dto.trip;

import lombok.Data;

@Data
public class TripPlaceDTO {
    private Long tripPlaceId;
    private Long placeId;
    private String title;
    private Integer placeOrder;
}
