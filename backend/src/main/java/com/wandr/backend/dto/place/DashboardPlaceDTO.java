package com.wandr.backend.dto.place;

import lombok.Data;

import java.util.List;

@Data
public class DashboardPlaceDTO {
    private Long id;
    private String name;
    private String description;
    private Double latitude;
    private Double longitude;
    private String address;
    private String image;
    private List<String> categories;
    private List<String> activities;
    //liked or not
    private boolean liked;
    private Integer rating;
}
