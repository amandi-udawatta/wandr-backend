package com.wandr.backend.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class Reservation {
    private Long reservationId;
    private Long travellerId;
    private Timestamp reservationDate;
    private Timestamp expirationDate;
    private BigDecimal totalAmount;

}

