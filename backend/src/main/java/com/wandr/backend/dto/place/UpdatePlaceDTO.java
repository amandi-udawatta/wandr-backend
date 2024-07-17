package com.wandr.backend.dto.place;

import lombok.Data;

@Data
public class UpdatePlaceDTO {
    private Long id;
    private String name;
    private String description;
    private String address;
}
