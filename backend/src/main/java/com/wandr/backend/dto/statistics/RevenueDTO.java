package com.wandr.backend.dto.statistics;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RevenueDTO {
    private BigDecimal premiumRevenue;
    private BigDecimal businessPlanRevenue;
    private BigDecimal reservationRevenue;
}

