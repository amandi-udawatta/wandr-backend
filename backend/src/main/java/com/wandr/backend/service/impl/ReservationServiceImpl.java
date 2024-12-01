package com.wandr.backend.service.impl;

import com.wandr.backend.dao.ReservationDAO;
import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.service.ReservationService;
import com.wandr.backend.dto.reservation.ReservationForBusinessDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationDAO reservationDAO;
    private final Logger logger = LoggerFactory.getLogger(ReservationServiceImpl.class);

    public ReservationServiceImpl(ReservationDAO reservationDAO) {
        this.reservationDAO = reservationDAO;
    }

//    @Override
//    // Method to mark reservation as purchased
//    public ApiResponse<String> markReservationAsPurchased(Long reservationId) {
//        int rowsUpdated = reservationDAO.markReservationAsPurchased(reservationId);
//        if (rowsUpdated > 0) {
//            return new ApiResponse<>(true, 200, "Reservation marked as purchased successfully", "Purchased");
//        } else {
//            return new ApiResponse<>(false, 404, "Reservation not found or already processed", null);
//        }
//    }
//
//    @Override
//    // Method to expire reservations and restore product count
//    public ApiResponse<String> expireReservations() {
//        int expiredReservations = reservationDAO.markExpiredReservations(LocalDateTime.now());
//        if (expiredReservations > 0) {
//            reservationDAO.restoreProductCountForExpiredReservations();
//            return new ApiResponse<>(true, 200, "Expired reservations processed successfully", "Expired and Restored");
//        } else {
//            return new ApiResponse<>(false, 404, "No active reservations to expire", null);
//        }
    //    }
    @Override
    public List<ReservationForBusinessDTO> getReservationsByProductId(int productId) {
        return reservationDAO.findReservationsByProductId(productId);
    }

    @Override
    public boolean updateReservationStatus(int reservationId, String status) {
        System.out.println("status" + status);
        return reservationDAO.updateReservationStatus(reservationId, status);
    }

    @Override
    public List<ReservationForBusinessDTO> getReservationsByBusinessId(int businessId) {
        return reservationDAO.findReservationsByBusinessId(businessId);
    }

}


//TODO: Create reservation method, reservations per traveller, purchased items per traveller
