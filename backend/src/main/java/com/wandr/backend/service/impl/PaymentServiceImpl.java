package com.wandr.backend.service.impl;

import com.wandr.backend.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${PAYHERE_MERCHANT_ID}")
    private String merchantId;

    @Value("${PAYHERE_MERCHANT_SECRET}")
    private String merchantSecret;

    @Override
    public String generatePaymentHash(String orderId, double amount, String currency) {
        try {
            String secretHash = md5(merchantSecret.toUpperCase());
            String data = merchantId + orderId + amount + currency + secretHash;
            return md5(data).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating hash: " + e.getMessage(), e);
        }
    }

    private String md5(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
