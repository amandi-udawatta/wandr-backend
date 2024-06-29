package com.wandr.backend.entity;

import lombok.Data;

import java.util.List;

@Data
public class Admin {

    private Long adminId;
    private String name;
    private String email;
    private String password;
    private String jwt;
    private String salt;

}
