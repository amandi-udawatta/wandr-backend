package com.wandr.backend.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class ReservedUnit {
    private Long unitId;
    private Long productId;
    private Long reservationId;
    private Integer quantity;
    private String reservationStatus; // Active, Expired, Purchased

}

