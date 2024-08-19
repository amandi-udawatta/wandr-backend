package com.wandr.backend.dto.trip;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TripDurationAndDistanceDTO {
    private Integer totalDistance;
    private Integer totalDuration;

}
