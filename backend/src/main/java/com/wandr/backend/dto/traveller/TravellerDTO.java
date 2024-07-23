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
    private List<Integer> categories;
    private List<Integer> activities;
    private Timestamp createdAt;
    private String membership;
}
