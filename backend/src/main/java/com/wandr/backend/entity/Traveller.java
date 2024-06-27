package com.wandr.backend.entity;

import lombok.Data;
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

}
