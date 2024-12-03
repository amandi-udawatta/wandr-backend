package com.wandr.backend.dto.payment;

import lombok.Data;


@Data
public class CreatePaymentIntentRequestDTO {
    private Long amount; // Amount in cents
}
