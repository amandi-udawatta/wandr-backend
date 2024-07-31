package com.wandr.backend.dto.traveller;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class TravellerDTO {
    private Long travellerId;
    private String name;
    private String email;
    private String country;
    private String profileImage;
    private List<Long> categories;
    private List<Long> activities;
    private Timestamp createdAt;
    private String membership;
}
