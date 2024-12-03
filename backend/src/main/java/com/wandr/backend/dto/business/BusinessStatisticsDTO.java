package com.wandr.backend.dto.business;

import lombok.Data;

@Data
public class BusinessStatisticsDTO {
    private int totalReservedProducts;
    private double totalRevenue;
    private int positiveFeedbacks;
    private int negativeFeedbacks;
    private int remainingDaysToEndPlan;
}
