package com.wandr.backend.dto.trip;

import lombok.Data;

import java.util.List;

@Data
public class ReorderTripDTO {
    private Long tripId;
    private List<PlaceOrderDTO> placeList;
    private double startLat;
    private double startLng;
    private double endLat;
    private double endLng;
}