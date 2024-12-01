package com.wandr.backend.mapper;

import com.wandr.backend.dto.reservation.ReservationForBusinessDTO;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReservationRowMapper implements RowMapper<ReservationForBusinessDTO> {
    @Override
    public ReservationForBusinessDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        ReservationForBusinessDTO dto = new ReservationForBusinessDTO();
        dto.setReservation_id(rs.getInt("reservation_id"));
        dto.setProduct_id(rs.getLong("product_id"));
        dto.setUnit_id(rs.getLong("unit_id"));
        dto.setTravellerName(rs.getString("name"));
        dto.setProductName(rs.getString("product_name"));
        dto.setQuantity(rs.getInt("quantity"));
        dto.setProductReservationPrice(rs.getBigDecimal("reservation_payment"));
        dto.setProductPrice(rs.getBigDecimal("price"));
        dto.setReservationStatus(rs.getString("reservation_status"));
        dto.setReservationDate(rs.getString("reservation_date"));
        dto.setExpirationDate(rs.getString("expiration_date"));

        // Calculate total reservation price
        BigDecimal reservationPayment = rs.getBigDecimal("reservation_payment");
        int quantity = rs.getInt("quantity");
        dto.setTotalReservationPrice(reservationPayment.multiply(BigDecimal.valueOf(quantity)));

        // Calculate total price using product price
        BigDecimal productPrice = rs.getBigDecimal("price");
        dto.setTotalPrice(productPrice.multiply(BigDecimal.valueOf(quantity)));

        return dto;
    }
}
