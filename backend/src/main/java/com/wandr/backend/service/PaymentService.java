package com.wandr.backend.service;

import com.wandr.backend.dto.payment.CreatePaymentIntentRequestDTO;
import com.wandr.backend.dto.payment.CreatePaymentIntentResponseDTO;

public interface PaymentService {
    CreatePaymentIntentResponseDTO createPaymentIntent(CreatePaymentIntentRequestDTO requestDTO);
}
