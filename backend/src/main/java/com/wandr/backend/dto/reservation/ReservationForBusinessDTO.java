package com.wandr.backend.dto.reservation;

import lombok.Data;


@Data
public class ReservationForBusinessDTO {
    private long reservation_id;
    private long unit_id;
    private String travellerName;
    private long product_id;
    private String productName;
    private int quantity; //number of products reserved
    private double productReservationPrice;
    private double productPrice;
    private double totalReservationPrice;
    private double totalPrice;
    private String reservationStatus;
    private String reservationDate;
    private String expirationDate;
//    private String image;
}


