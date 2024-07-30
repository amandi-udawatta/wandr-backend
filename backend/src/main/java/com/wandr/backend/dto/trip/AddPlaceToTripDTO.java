package com.wandr.backend.dto.trip;

import lombok.Data;

@Data
public class AddPlaceToTripDTO {
    private Long tripId;
    private Long placeId;

}
