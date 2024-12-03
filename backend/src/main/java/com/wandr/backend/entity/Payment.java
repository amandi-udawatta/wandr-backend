package com.wandr.backend.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class Payment {
        private Long id;
        private String refId;
        private String type; // PLAN or RESERVATION
        private Long userId;
        private String role; // TRAVELLER or BUSINESS
        private Timestamp date;
        private BigDecimal amount;
        private String paymentStatus; // PAID, FAILED, etc.
        private Long planId; // Nullable
}
