package com.wandr.backend.dto;

import lombok.Data;

@Data
public class TravellerSignupDTO {
    private String name;
    private String email;
    private String password;
    private String country;
}
