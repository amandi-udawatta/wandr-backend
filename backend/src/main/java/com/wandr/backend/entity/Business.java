package com.wandr.backend.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
public class Business {
        private Long businessId;
        private String name;
        private String email;
        private String password;
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
        private Integer businessType;
        private Integer shopCategory;
        private String ownerName;
        private String ownerContact;
        private String ownerNic;
        private String status;
        private String jwt;
        private String salt;
        private Timestamp createdAt;
        private Integer planId;
        private Timestamp paidDate;
        private Timestamp planEndDate;
        private Integer rating;

}
