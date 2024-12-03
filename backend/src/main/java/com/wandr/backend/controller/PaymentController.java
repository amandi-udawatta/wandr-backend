package com.wandr.backend.controller;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.payment.CreatePaymentIntentRequestDTO;
import com.wandr.backend.dto.payment.CreatePaymentIntentResponseDTO;
import com.wandr.backend.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/stripe")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-payment-intent")
    public ResponseEntity<ApiResponse<CreatePaymentIntentResponseDTO>> createPaymentIntent(
             @RequestBody CreatePaymentIntentRequestDTO requestDTO) {

        CreatePaymentIntentResponseDTO responseDTO = paymentService.createPaymentIntent(requestDTO);
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "Payment Intent created successfully", responseDTO));
    }
}
