package com.wandr.backend.service;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.reservation.ReservationForBusinessDTO;
//import com.wandr.backend.dto.ads.AdDTO;
//import com.wandr.backend.dto.ads.ApprovedAdDTO;


import java.util.List;

public interface ReservationService {

//    ApiResponse<String> markReservationAsPurchased(Long reservationId);
//    ApiResponse<String> expireReservations();
    List<ReservationForBusinessDTO> getReservationsByProductId(int productId);
    boolean updateReservationStatus(int reservationId, String status);
}
