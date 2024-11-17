package com.wandr.backend.dto.admin;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RevenueDTO {
    private BigDecimal premiumRevenue;
    private BigDecimal businessPlanRevenue;
    private BigDecimal reservationRevenue;
}

