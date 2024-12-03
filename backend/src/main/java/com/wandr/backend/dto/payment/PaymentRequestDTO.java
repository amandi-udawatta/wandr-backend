package com.wandr.backend.dto.payment;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequestDTO {

    private String refId;

    private String type; // PLAN or RESERVATION

    private Long userId;

    private String role; // TRAVELLER or BUSINESS

    private BigDecimal amount;

    private String paymentStatus;

    private Long planId; // Optional
}
