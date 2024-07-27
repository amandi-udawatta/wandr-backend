package com.wandr.backend.dto.ads;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ApprovedAdDTO {
    private Long adId;
    private String shopName;
    private Long businessId;
    private String title;
    private String description;
    private String image;
    private String businessPlan;
    private Timestamp postedDate;
    private int remainingDays;
    private String status;
}


