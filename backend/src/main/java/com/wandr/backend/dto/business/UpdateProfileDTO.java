package com.wandr.backend.dto.business;

import lombok.Data;

import java.util.List;

@Data
public class UpdateProfileDTO {
    private String name;
    private String password;
    private String email;
    private String description;
    private List<String> services;
    private String address;
    private List<String> languages;
    private String websiteUrl;
    private String businessContact;
    private String shopImage;
    private Integer categoryId;
    private String ownerName;
    private String ownerContact;
    private String ownerNic;
}
