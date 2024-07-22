package com.wandr.backend.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Ad {

        private Long adId;
        private Long businessId;
        private String title;
        private String description;
        private String image;
        private Timestamp requestedDate;
        private String status;
}
