package com.wandr.backend.service;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.payment.CreatePaymentIntentRequestDTO;
import com.wandr.backend.dto.payment.CreatePaymentIntentResponseDTO;
import com.wandr.backend.dto.payment.PaymentRequestDTO;

public interface PaymentSaveService {
    ApiResponse<Void> processPayment(PaymentRequestDTO paymentRequestDTO);
}
