package com.wandr.backend.dao;

import com.wandr.backend.dto.reservation.ReservationForBusinessDTO;
import com.wandr.backend.entity.Reservation;
import com.wandr.backend.entity.ReservedUnit;
import com.wandr.backend.mapper.ReservationRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReservationDAO {

    private final JdbcTemplate jdbcTemplate;
    private final Logger logger = LoggerFactory.getLogger(ReservationDAO.class);
    public ReservationDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long createReservation(Reservation reservation) {
        String sql = "INSERT INTO reservations (traveller_id, reservation_date, expiration_date, total_amount) VALUES (?, ?, ?, ?) RETURNING reservation_id";
        return jdbcTemplate.queryForObject(sql, Long.class, reservation.getTravellerId(), reservation.getReservationDate(),
                reservation.getExpirationDate(), reservation.getTotalAmount());
    }

    public void createReservedUnit(ReservedUnit reservedUnit) {
        String sql = "INSERT INTO reserved_units (product_id, reservation_id, quantity, reservation_status) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, reservedUnit.getProductId(), reservedUnit.getReservationId(),
                reservedUnit.getQuantity(), reservedUnit.getReservationStatus());
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

    public List<ReservationForBusinessDTO> findReservationsByProductId(int productId) {
        String query = """
        SELECT r.reservation_id, t.name, p.name AS product_name, ru.quantity, ru.unit_id,
               r.total_amount, ru.reservation_status, r.reservation_date, r.expiration_date, 
               p.product_id, p.reservation_payment, p.price
        FROM reservations r
        JOIN reserved_units ru ON r.reservation_id = ru.reservation_id
        JOIN products p ON ru.product_id = p.product_id
        JOIN travellers t ON r.traveller_id = t.traveller_id
        WHERE p.product_id = ?
    """;

        return jdbcTemplate.query(query, new ReservationRowMapper(), productId);
    }

    public boolean updateReservationStatus(long reservationUnitId, String status) {
        String updateQuery = "UPDATE reserved_units SET reservation_status = ? WHERE unit_id = ?";
        int rowsUpdated = jdbcTemplate.update(updateQuery, status, reservationUnitId);
        System.out.println("rowsUpdated = " + rowsUpdated);
        return rowsUpdated > 0;
    }

    public List<ReservationForBusinessDTO> findReservationsByBusinessId(int businessId) {
        String query = """
        SELECT r.reservation_id, t.name, p.name AS product_name, ru.quantity, ru.unit_id,
               r.total_amount, ru.reservation_status, r.reservation_date, r.expiration_date, 
               p.product_id, p.reservation_payment, p.price
            FROM reservations r
            JOIN reserved_units ru ON r.reservation_id = ru.reservation_id
            JOIN products p ON ru.product_id = p.product_id
            JOIN travellers t ON r.traveller_id = t.traveller_id
            JOIN businesses b ON p.business_id = b.business_id
            WHERE b.business_id = ?
        """;
        return jdbcTemplate.query(query, new ReservationRowMapper() ,businessId);
    }

    // Fetch Reserved Items (Active Status)
    public List<ReservationForBusinessDTO> findReservedItemsByTravellerId(Long travellerId) {
        String sql = """
        SELECT r.reservation_id, t.name, p.name AS product_name, ru.quantity, ru.unit_id,
               ru.reservation_status, r.reservation_date, r.expiration_date, 
               p.product_id, p.reservation_payment, p.price
        FROM reservations r
        JOIN reserved_units ru ON r.reservation_id = ru.reservation_id
        JOIN products p ON ru.product_id = p.product_id
        JOIN travellers t ON r.traveller_id = t.traveller_id
        WHERE r.traveller_id = ? AND ru.reservation_status = 'active'
    """;
        return jdbcTemplate.query(sql, new ReservationRowMapper(), travellerId);
    }

    // Fetch Purchased Items
    public List<ReservationForBusinessDTO> findPurchasedItemsByTravellerId(Long travellerId) {
        String sql = """
        SELECT r.reservation_id, t.name, p.name AS product_name, ru.quantity, ru.unit_id,
               ru.reservation_status, r.reservation_date, r.expiration_date, 
               p.product_id, p.reservation_payment, p.price
        FROM reservations r
        JOIN reserved_units ru ON r.reservation_id = ru.reservation_id
        JOIN products p ON ru.product_id = p.product_id
        JOIN travellers t ON r.traveller_id = t.traveller_id
        WHERE r.traveller_id = ? AND ru.reservation_status = 'purchased'
    """;
        return jdbcTemplate.query(sql, new ReservationRowMapper(), travellerId);
    }

}
