package com.wandr.backend.dto.trip;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class PendingTripsDTO {
    private Long tripId;
    private String name;
    private Long routeType;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private List<TripPlaceDTO> tripPlaces;
    private Integer orderedTime;
    private Integer optimizedTime;
    private Integer orderedDistance;
    private Integer optimizedDistance;
    private double start_lat;
    private double start_lng;
    private double end_lat;
    private double end_lng;

}
