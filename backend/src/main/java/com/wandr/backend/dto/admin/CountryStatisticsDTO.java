package com.wandr.backend.dto.admin;

import lombok.Data;

@Data
public class CountryStatisticsDTO {
    private String country;
    private long userCount;
}

