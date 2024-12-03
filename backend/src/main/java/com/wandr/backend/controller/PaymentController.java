package com.wandr.backend.controller;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.payment.GenerateHashRequestDTO;
import com.wandr.backend.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/generate-hash")
    public ResponseEntity<ApiResponse<String>> generateHash(
            @RequestBody GenerateHashRequestDTO request) {

        String hash = paymentService.generatePaymentHash(request.getOrderId(), request.getAmount(), request.getCurrency());
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "Hash generated successfully", hash));
    }
}
