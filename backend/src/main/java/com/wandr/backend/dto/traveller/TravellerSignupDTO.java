package com.wandr.backend.dto.traveller;

import lombok.Data;

@Data
public class TravellerSignupDTO {
    private String name;
    private String email;
    private String password;
    private String country;
    private String salt;

}
