package com.wandr.backend.service.impl;

import com.wandr.backend.dao.ReservationDAO;
import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationDAO reservationDAO;

    @Autowired
    public ReservationServiceImpl(ReservationDAO reservationDAO) {
        this.reservationDAO = reservationDAO;
    }

    @Override
    // Method to mark reservation as purchased
    public ApiResponse<String> markReservationAsPurchased(Long reservationId) {
        int rowsUpdated = reservationDAO.markReservationAsPurchased(reservationId);
        if (rowsUpdated > 0) {
            return new ApiResponse<>(true, 200, "Reservation marked as purchased successfully", "Purchased");
        } else {
            return new ApiResponse<>(false, 404, "Reservation not found or already processed", null);
        }
    }

    @Override
    // Method to expire reservations and restore product count
    public ApiResponse<String> expireReservations() {
        int expiredReservations = reservationDAO.markExpiredReservations(LocalDateTime.now());
        if (expiredReservations > 0) {
            reservationDAO.restoreProductCountForExpiredReservations();
            return new ApiResponse<>(true, 200, "Expired reservations processed successfully", "Expired and Restored");
        } else {
            return new ApiResponse<>(false, 404, "No active reservations to expire", null);
        }
    }
}
