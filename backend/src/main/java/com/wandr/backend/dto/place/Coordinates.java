package com.wandr.backend.dto.place;

import lombok.Data;

@Data
public class Coordinates {
    private final double latitude;
    private final double longitude;

    @Override
    public String toString() {
        return latitude + "," + longitude;
    }
}
