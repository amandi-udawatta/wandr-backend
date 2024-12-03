package com.wandr.backend.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.sql.In;

import java.sql.Timestamp;

@Data
@Getter
@Setter
public class Ad {

        private Long adId;
        private Long businessId;
        private String title;
        private String description;
        private String image;
        private Timestamp requestedDate;
        private String status;
        private Timestamp adStartDate;
        private Timestamp adExpirationDate;
        private boolean isActive;
        private Integer viewCount;
        private Integer clickCount;

}