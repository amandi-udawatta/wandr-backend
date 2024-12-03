package com.wandr.backend.mapper;

import com.wandr.backend.dto.reservation.ReservationForBusinessDTO;
import org.springframework.jdbc.core.RowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReservationRowMapper implements RowMapper<ReservationForBusinessDTO> {

    private final Logger logger = LoggerFactory.getLogger(ReservationRowMapper.class);

    @Override
    public ReservationForBusinessDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        logger.info("Mapping reservation row to ReservationForBusinessDTO");
        ReservationForBusinessDTO dto = new ReservationForBusinessDTO();
        dto.setReservation_id(rs.getInt("reservation_id"));
        logger.info("Reservation ID: {}", rs.getInt("reservation_id"));
        dto.setProduct_id(rs.getLong("product_id"));
        logger.info("Product ID: {}", rs.getLong("product_id"));
        dto.setUnit_id(rs.getLong("unit_id"));
        logger.info("Unit ID: {}", rs.getLong("unit_id"));
        dto.setTravellerName(rs.getString("name"));
        logger.info("Traveller Name: {}", rs.getString("name"));
        dto.setProductName(rs.getString("product_name"));
        logger.info("Product Name: {}", rs.getString("product_name"));
        dto.setQuantity(rs.getInt("quantity"));
        logger.info("Quantity: {}", rs.getInt("quantity"));
        dto.setProductReservationPrice(rs.getBigDecimal("reservation_payment"));
        logger.info("Product Reservation Price: {}", rs.getBigDecimal("reservation_payment"));
        dto.setProductPrice(rs.getBigDecimal("price"));
        logger.info("Product Price: {}", rs.getBigDecimal("price"));
        dto.setReservationStatus(rs.getString("reservation_status"));
        logger.info("Reservation Status: {}", rs.getString("reservation_status"));
        dto.setReservationDate(rs.getString("reservation_date"));
        logger.info("Reservation Date: {}", rs.getString("reservation_date"));
        dto.setExpirationDate(rs.getString("expiration_date"));
        logger.info("Expiration Date: {}", rs.getString("expiration_date"));

        // Calculate total reservation price
        BigDecimal reservationPayment = rs.getBigDecimal("reservation_payment");
        logger.info("Reservation Payment: {}", reservationPayment);
        int quantity = rs.getInt("quantity");
        logger.info("Quantity: {}", quantity);
        dto.setTotalReservationPrice(reservationPayment.multiply(BigDecimal.valueOf(quantity)));
        logger.info("Total Reservation Price: {}", dto.getTotalReservationPrice());

        // Calculate total price using product price
        BigDecimal productPrice = rs.getBigDecimal("price");
        logger.info("Product Price: {}", productPrice);
        dto.setTotalPrice(productPrice.multiply(BigDecimal.valueOf(quantity)));
        logger.info("Total Price: {}", dto.getTotalPrice());

        logger.info("Mapped reservation row to ReservationForBusinessDTO: {}", dto);

        return dto;
    }
}
