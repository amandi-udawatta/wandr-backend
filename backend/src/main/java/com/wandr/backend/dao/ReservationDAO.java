package com.wandr.backend.dao;

import com.wandr.backend.dto.reservation.ReservationForBusinessDTO;
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

        return jdbcTemplate.query(query, new Object[]{productId}, (ResultSet rs) -> {
            List<ReservationForBusinessDTO> reservations = new ArrayList<>();
            while (rs.next()) {
                ReservationForBusinessDTO dto = new ReservationForBusinessDTO();
                dto.setReservation_id(rs.getInt("reservation_id"));
                dto.setProduct_id(rs.getLong("product_id"));
                dto.setUnit_id(rs.getLong("unit_id"));
                dto.setTravellerName(rs.getString("name"));
                dto.setProductName(rs.getString("product_name"));
                dto.setQuantity(rs.getInt("quantity"));
                dto.setProductReservationPrice(rs.getDouble("reservation_payment"));
                dto.setProductPrice(rs.getDouble("price"));
                dto.setReservationStatus(rs.getString("reservation_status"));
                dto.setReservationDate(rs.getString("reservation_date"));
                dto.setExpirationDate(rs.getString("expiration_date"));

                // Calculate total price by multiplying price and quantity
                double reservation_price = rs.getDouble("reservation_payment");
                int quantity = rs.getInt("quantity");
                double totalReservationPrice = reservation_price * quantity;
                dto.setTotalReservationPrice(totalReservationPrice); // Assuming there’s a setter for total price

                double price = rs.getDouble("price");
                dto.setTotalPrice(price*quantity);

                reservations.add(dto);
            }
            return reservations;
        });
    }

    public boolean updateReservationStatus(int reservationId, String status) {
        logger.info("came to update reservation_status");
        String updateQuery = """
            UPDATE reserved_units
            SET reservation_status = ?
            WHERE unit_id = ?
        """;
        int rowsUpdated = jdbcTemplate.update(updateQuery, status, reservationId);
        return rowsUpdated > 0;
    }

}
