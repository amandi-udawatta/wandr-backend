package com.wandr.backend.controller;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.payment.CreatePaymentIntentRequestDTO;
import com.wandr.backend.dto.payment.CreatePaymentIntentResponseDTO;
import com.wandr.backend.dto.payment.PaymentRequestDTO;
import com.wandr.backend.dto.payment.PaymentResponseDTO;
import com.wandr.backend.service.PaymentSaveService;
import com.wandr.backend.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/stripe")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentSaveService paymentSaveService;
    @Autowired
    public PaymentController(PaymentService paymentService, PaymentSaveService paymentSaveService) {
        this.paymentService = paymentService;
        this.paymentSaveService = paymentSaveService;
    }

    @PostMapping("/create-payment-intent")
    public ResponseEntity<ApiResponse<CreatePaymentIntentResponseDTO>> createPaymentIntent(
             @RequestBody CreatePaymentIntentRequestDTO requestDTO) {

        CreatePaymentIntentResponseDTO responseDTO = paymentService.createPaymentIntent(requestDTO);
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "Payment Intent created successfully", responseDTO));
    }

    @PostMapping("/save-payment")
    public ResponseEntity<ApiResponse<Void>> savePayment(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        try {
            ApiResponse<Void> responseDTO = paymentSaveService.processPayment(paymentRequestDTO);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "Error processing payment", null));
        }
    }

}
