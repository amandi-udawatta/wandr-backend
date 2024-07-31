package com.wandr.backend.dto.recommendation;

import lombok.Data;

import java.util.List;

@Data
public class PlacesDTO {
    private Long id;
    private String name;
    private List<String> categories;
    private List<String> activities;
}


