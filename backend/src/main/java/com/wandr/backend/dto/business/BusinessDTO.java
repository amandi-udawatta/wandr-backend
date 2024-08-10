package com.wandr.backend.dto.business;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
public class BusinessDTO {
    private Long businessId;
    private String name;
    private String email;
    private String description;
    private List<String> services;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private List<String> languages;
    private String websiteUrl;
    private String businessContact;
    private String shopImage;
    private String profileImage;
    private String businessType;
    private String ownerName;
    private String ownerContact;
    private String ownerNic;
    //created at
    private Timestamp createdAt;
    private String shopCategory;
    private String plan;
    private String status;
    private Integer rating;

}
