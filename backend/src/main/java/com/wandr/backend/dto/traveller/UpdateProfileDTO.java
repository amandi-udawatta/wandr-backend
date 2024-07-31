package com.wandr.backend.dto.traveller;

import lombok.Data;

import java.util.List;

@Data
public class UpdateProfileDTO {
    private String name;
    private String email;
    private String country;
    private String profileImage;  // This will be the URL or filename of the profile image
    private List<Long> categories;
    private List<Long> activities;
    private String membership;
}
