package com.wandr.backend.dto.trip;

import lombok.Data;

import java.util.List;

@Data
public class ReorderTripDTO {
    private Long tripId;
    private List<PlaceOrderDTO> placeList;
}