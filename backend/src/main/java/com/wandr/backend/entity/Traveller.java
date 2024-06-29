package com.wandr.backend.entity;

import lombok.Data;
import java.sql.Timestamp;
import java.util.List;

@Data
public class Traveller {

    private Long travellerId;
    private String name;
    private String email;
    private String password;
    private String country;
    private List<Integer> categories;
    private List<Integer> activities;
    private String profileImage;
    private Timestamp createdAt;
    private String membership;
    private String jwt;
    private String salt;

}
