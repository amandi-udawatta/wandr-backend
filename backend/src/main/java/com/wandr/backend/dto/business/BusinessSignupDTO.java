package com.wandr.backend.dto.business;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class BusinessSignupDTO {
    private String name;
    private String email;
    private String password;
    private String description;
    private List<String> services;
    private String address;
    private List<String> languages;
    private String websiteUrl;
    private String businessContact;
    private Integer businessType;
    private Integer shopCategory;
    private String ownerName;
    private String ownerContact;
    private String ownerNic;
    private String salt;
    private MultipartFile shopImage;

}
