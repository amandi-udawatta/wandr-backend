package com.wandr.backend.dto;

import lombok.Data;

@Data
public class BusinessRatingDTO {
    private Long travellerId;
    private Long businessId;
    private Integer rating;
}
