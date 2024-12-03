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

    // Mark reserved units as expired and restore product count
    public int markExpiredReservations(LocalDateTime currentTime) {
        // Fetch all reserved units that are still active but expired
        String fetchExpiredUnitsQuery = """
        SELECT ru.unit_id, ru.product_id, ru.quantity
        FROM reserved_units ru
        JOIN reservations r ON ru.reservation_id = r.reservation_id
        WHERE ru.reservation_status = 'active' 
          AND r.expiration_date < ?
    """;

        List<ReservedUnit> expiredUnits = jdbcTemplate.query(fetchExpiredUnitsQuery, (rs, rowNum) -> {
            ReservedUnit unit = new ReservedUnit();
            unit.setUnitId(rs.getLong("unit_id"));
            unit.setProductId(rs.getLong("product_id"));
            unit.setQuantity(rs.getInt("quantity"));
            return unit;
        }, currentTime);

        // Mark these reserved units as expired
        String updateReservedUnitsQuery = """
        UPDATE reserved_units 
        SET reservation_status = 'expired' 
        WHERE unit_id = ?
    """;

        // Restore product quantities for expired units
        String updateProductQuantityQuery = """
        UPDATE products 
        SET quantity = quantity + ? 
        WHERE product_id = ?
    """;

        for (ReservedUnit unit : expiredUnits) {
            // Mark reserved unit as expired
            jdbcTemplate.update(updateReservedUnitsQuery, unit.getUnitId());

            // Restore product quantity
            jdbcTemplate.update(updateProductQuantityQuery, unit.getQuantity(), unit.getProductId());
        }

        // Return the number of expired units
        return expiredUnits.size();
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
