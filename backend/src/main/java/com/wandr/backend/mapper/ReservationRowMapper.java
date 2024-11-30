package com.wandr.backend.mapper;

import com.wandr.backend.dto.reservation.ReservationForBusinessDTO;
import org.springframework.jdbc.core.RowMapper;

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
        dto.setProductReservationPrice(rs.getDouble("reservation_payment"));
        dto.setProductPrice(rs.getDouble("price"));
        dto.setReservationStatus(rs.getString("reservation_status"));
        dto.setReservationDate(rs.getString("reservation_date"));
        dto.setExpirationDate(rs.getString("expiration_date"));

        // Calculate total reservation price
        double reservationPayment = rs.getDouble("reservation_payment");
        int quantity = rs.getInt("quantity");
        dto.setTotalReservationPrice(reservationPayment * quantity);

        // Calculate total price using product price
        double productPrice = rs.getDouble("price");
        dto.setTotalPrice(productPrice * quantity);

        return dto;
    }
}
