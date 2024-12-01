package com.wandr.backend.dto.trip;

import lombok.Data;

@Data
public class TripTimeDTO {
    private Long tripId;
    private Integer orderedTime;
    private Integer optimizedTime;
    private Integer orderedDistance;
    private Integer optimizedDistance;
    private Integer estimatedOrderedTime;
    private Integer estimatedOptimizedTime;
}
