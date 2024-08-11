package com.wandr.backend.dto.trip;

import lombok.Data;

@Data
public class ShortestTripDTO {
    private Long tripId;
    private double startLat;
    private double startLng;
    private double endLat;
    private double endLng;
}