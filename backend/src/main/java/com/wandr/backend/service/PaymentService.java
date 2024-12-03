package com.wandr.backend.service;

public interface PaymentService {
    String generatePaymentHash(String orderId, double amount, String currency);
}
