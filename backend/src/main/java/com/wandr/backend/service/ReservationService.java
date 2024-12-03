package com.wandr.backend.service;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.reservation.CreateReservationRequestDTO;
import com.wandr.backend.dto.reservation.ReservationForBusinessDTO;
//import com.wandr.backend.dto.ads.AdDTO;
//import com.wandr.backend.dto.ads.ApprovedAdDTO;


import java.util.List;

public interface ReservationService {

    ApiResponse<String> expireReservations();
    List<ReservationForBusinessDTO> getReservationsByProductId(int productId);
    boolean updateReservationStatus(long reservationUnitId, String status);
    List<ReservationForBusinessDTO> getReservationsByBusinessId(int businessId);

    ApiResponse<Void> createReservation(CreateReservationRequestDTO requestDTO);

    List<ReservationForBusinessDTO> getReservedItemsByTravellerId(Long travellerId);

    List<ReservationForBusinessDTO> getPurchasedItemsByTravellerId(Long travellerId);

    }
