package com.wandr.backend.mapper;

import com.wandr.backend.dto.reservation.ReservationForBusinessDTO;
import com.wandr.backend.entity.Product;
import org.springframework.jdbc.core.RowMapper;

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
        dto.setProductReservationPrice(rs.getDouble("unit_price"));
        dto.setReservationStatus(rs.getString("reservation_status"));
        dto.setReservationDate(rs.getString("reservation_date"));
        dto.setExpirationDate(rs.getString("expiration_date"));
        return dto;
    }
}
