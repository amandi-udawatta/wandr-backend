package com.wandr.backend.service.impl;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.wandr.backend.dao.BusinessDAO;
import com.wandr.backend.dao.PaymentDAO;
import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.payment.CreatePaymentIntentRequestDTO;
import com.wandr.backend.dto.payment.CreatePaymentIntentResponseDTO;
import com.wandr.backend.dto.payment.PaymentRequestDTO;
import com.wandr.backend.entity.Payment;
import com.wandr.backend.service.PaymentSaveService;
import com.wandr.backend.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class PaymentSaveServiceImpl implements PaymentSaveService {

    private final PaymentDAO paymentDAO;
    private final BusinessDAO businessDAO;


    @Autowired
    public PaymentSaveServiceImpl(PaymentDAO paymentDAO, BusinessDAO businessDAO) {
        this.paymentDAO = paymentDAO;
        this.businessDAO = businessDAO;
    }

    @Override
    public ApiResponse<Void> processPayment(PaymentRequestDTO paymentRequestDTO) {
        // Map DTO to Entity
        Payment payment = new Payment();
        payment.setRefId(paymentRequestDTO.getRefId());
        payment.setType(paymentRequestDTO.getType());
        payment.setUserId(paymentRequestDTO.getUserId());
        payment.setRole(paymentRequestDTO.getRole());
        payment.setDate(new Timestamp(System.currentTimeMillis()));
        payment.setAmount(paymentRequestDTO.getAmount());
        payment.setPaymentStatus(paymentRequestDTO.getPaymentStatus());
        payment.setPlanId(paymentRequestDTO.getPlanId());

        // Save Payment
        paymentDAO.save(payment);

        // If the role is BUSINESS and the payment is for a plan, update the business status
        if ("BUSINESS".equals(paymentRequestDTO.getRole()) && "PLAN".equals(paymentRequestDTO.getType())) {
            businessDAO.updateStatus(paymentRequestDTO.getUserId(), paymentRequestDTO.getPlanId());

        }

        return new ApiResponse<>(true, 200, "Payment processed successfully", null);

    }





}
