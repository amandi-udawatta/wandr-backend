package com.wandr.backend.dto.business;

import lombok.Data;

@Data
public class OngoingAdvertisementDTO {
    private String adImage;
    private String adTitle;
    private int remainingDaysToEndAd;
}
