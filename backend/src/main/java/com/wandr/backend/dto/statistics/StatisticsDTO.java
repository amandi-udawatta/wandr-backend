package com.wandr.backend.dto.statistics;

import lombok.Data;

@Data
public class StatisticsDTO {
    private long totalBusinesses;
    private long totalReservations;
    private long totalAppDownloads;
    private long premiumAccounts;
    private long totalTrips;
}

