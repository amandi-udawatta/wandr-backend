package com.wandr.backend.dto.ads;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class AdDTO {
    private String shopName;
    private Long businessId;
    private String title;
    private String description;
    private String image;
    private String businessPlan;
    private Timestamp requestedDate;
    private String status;
}


