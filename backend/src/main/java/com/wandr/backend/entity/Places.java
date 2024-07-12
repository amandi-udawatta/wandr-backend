package com.wandr.backend.entity;

import lombok.Data;

import java.util.List;

@Data
public class Places {
    private Long id;
    private String name;
    private String description;
    private double latitude;
    private double longitude;
    private String address;
    private List<Integer> category;
    private List<Integer> activities;
}
