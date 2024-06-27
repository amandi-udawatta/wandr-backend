package com.wandr.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdateProfileDTO {
    private String name;
    private String email;
    private String country;
    private String profileImage;  // This will be the URL or filename of the profile image
    private List<Integer> categories;
    private List<Integer> activities;
}
