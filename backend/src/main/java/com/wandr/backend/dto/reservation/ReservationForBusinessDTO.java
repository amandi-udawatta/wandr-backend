package com.wandr.backend.dto.reservation;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class ReservationForBusinessDTO {
    private long reservation_id;
    private long unit_id;
    private String travellerName;
    private long product_id;
    private String productName;
    private int quantity; //number of products reserved
    private BigDecimal productReservationPrice;
    private BigDecimal productPrice;
    private BigDecimal totalReservationPrice;
    private BigDecimal totalPrice;
    private String reservationStatus;
    private String reservationDate;
    private String expirationDate;
//    private String image;
}


