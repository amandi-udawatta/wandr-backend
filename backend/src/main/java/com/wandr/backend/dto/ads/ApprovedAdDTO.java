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
    private Timestamp requestedDate;
    private Timestamp adStartDate; // Added
    private Timestamp adExpirationDate; // Added
    private int remainingDays;
    private String status;
    private boolean isActive;
    private Integer clickCount;

}
