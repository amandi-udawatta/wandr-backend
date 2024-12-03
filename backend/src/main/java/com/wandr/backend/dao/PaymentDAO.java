package com.wandr.backend.dao;

import com.wandr.backend.entity.Payment;
import com.wandr.backend.entity.Product;
import com.wandr.backend.mapper.ProductRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PaymentDAO {

    private final JdbcTemplate jdbcTemplate;

    public PaymentDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final Logger logger = LoggerFactory.getLogger(PaymentDAO.class);

    public void save(Payment payment) {
        String sql = "INSERT INTO payments (ref_id, type, user_id, role, date, amount, payment_status, plan_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                payment.getRefId(),
                payment.getType(),
                payment.getUserId(),
                payment.getRole(),
                payment.getDate(),
                payment.getAmount(),
                payment.getPaymentStatus(),
                payment.getPlanId());
    }
}
