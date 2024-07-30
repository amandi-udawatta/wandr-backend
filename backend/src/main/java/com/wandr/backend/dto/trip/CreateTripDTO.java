package com.wandr.backend.dto.trip;

import lombok.Data;

@Data
public class CreateTripDTO {
    private Integer travellerId;
    private String name;
    private Long placeId;
}
