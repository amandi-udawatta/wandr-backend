package com.wandr.backend.mapper;

import com.wandr.backend.entity.Payment;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentRowMapper implements RowMapper<Payment> {
    @Override
    public Payment mapRow(ResultSet rs, int rowNum) throws SQLException {
        Payment payment = new Payment();
        payment.setId(rs.getLong("id"));
        payment.setRefId(rs.getString("ref_id"));
        payment.setType(rs.getString("type"));
        payment.setUserId(rs.getLong("user_id"));
        payment.setRole(rs.getString("role"));
        payment.setDate(rs.getTimestamp("date"));
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setPaymentStatus(rs.getString("payment_status"));
        payment.setPlanId(rs.getLong("plan_id"));
        return payment;
    }
}
