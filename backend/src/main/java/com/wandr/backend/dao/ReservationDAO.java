package com.wandr.backend.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class ReservationDAO {

    private final JdbcTemplate jdbcTemplate;

    public ReservationDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Update reservation status to 'Purchased'
    public int markReservationAsPurchased(Long reservationId) {
        String sql = "UPDATE reservations SET status = 'Purchased' WHERE reservation_id = ? AND status = 'Active'";
        return jdbcTemplate.update(sql, reservationId);
    }

    // Update reservation status to 'Expired' for expired reservations
    public int markExpiredReservations(LocalDateTime currentTime) {
        String sql = "UPDATE reservations SET status = 'Expired' " +
                "WHERE status = 'Active' AND expiration_date < ?";
        return jdbcTemplate.update(sql, currentTime);
    }

    // Restore product counts for expired reservations
    public void restoreProductCountForExpiredReservations() {
        String sql = "UPDATE products p " +
                "SET quantity = quantity + 1 " +
                "FROM reserved_units ru " +
                "JOIN reservations r ON ru.reservation_id = r.reservation_id " +
                "WHERE r.status = 'Expired' AND p.product_id = ru.product_id";
        jdbcTemplate.update(sql);
    }
}
