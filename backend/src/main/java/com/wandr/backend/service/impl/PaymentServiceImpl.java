package com.wandr.backend.service.impl;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.wandr.backend.dto.payment.CreatePaymentIntentRequestDTO;
import com.wandr.backend.dto.payment.CreatePaymentIntentResponseDTO;
import com.wandr.backend.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${STRIPE_API_KEY}")
    private String stripeApiKey;

    @Value("${STRIPE_API_VERSION}")
    private String stripeApiVersion;

    public PaymentServiceImpl(@Value("${STRIPE_API_KEY}") String stripeApiKey) {
        Stripe.apiKey = stripeApiKey;
        Stripe.setAppInfo("Wandr", "1.0", null);
    }

    @Override
    public CreatePaymentIntentResponseDTO createPaymentIntent(CreatePaymentIntentRequestDTO requestDTO) {
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(requestDTO.getAmount()) // Amount in cents
                    .setCurrency("usd") // Change as needed
                    .addPaymentMethodType("card")
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);
            return new CreatePaymentIntentResponseDTO(paymentIntent.getClientSecret());
        } catch (Exception e) {
            throw new RuntimeException("Error creating payment intent: " + e.getMessage(), e);
        }
    }


}
