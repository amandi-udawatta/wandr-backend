package com.wandr.backend.dto.statistics;

import lombok.Data;

@Data
public class CountryStatisticsDTO {
    private String country;
    private long userCount;
}

