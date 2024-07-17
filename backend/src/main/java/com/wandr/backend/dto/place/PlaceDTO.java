package com.wandr.backend.dto.place;

import lombok.Data;

@Data
public class PlaceDTO {
    private Long id;
    private String name;
    private String description;
    private Double latitude;
    private Double longitude;
    private String address;
    private String image;
}
