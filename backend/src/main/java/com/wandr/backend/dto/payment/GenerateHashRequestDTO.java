package com.wandr.backend.dto.payment;

import lombok.Data;

@Data
public class GenerateHashRequestDTO {

    private String orderId;
    private Double amount;
    private String currency;
}
