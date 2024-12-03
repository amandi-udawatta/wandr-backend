package com.wandr.backend.dto.place;

import lombok.Data;

@Data
public class placeRatingDTO {
    private Long travellerId;
    private Long placeId;
    private Integer rating;
}
