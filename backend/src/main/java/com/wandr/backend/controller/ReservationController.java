package com.wandr.backend.controller;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.product.ProductDTO;
import com.wandr.backend.dto.reservation.CreateReservationRequestDTO;
import com.wandr.backend.dto.reservation.ReservationForBusinessDTO;
import com.wandr.backend.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Void>> createReservation(@RequestBody CreateReservationRequestDTO request) {
        try{
            ApiResponse<Void> response = reservationService.createReservation(request);
            logger.info("Successfully received reservation request for traveller ID: {}", request.getTravellerId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error receiving reservation request for traveller ID: {}", request.getTravellerId(), e);
            return ResponseEntity.ok(new ApiResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error receiving reservation request", null));
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<ReservationForBusinessDTO>>>  getReservationsByProductId(@PathVariable int productId) {
        logger.info("Successfully received reservations by product id {}", productId);
        try {
            List<ReservationForBusinessDTO> reservations = reservationService.getReservationsByProductId(productId);
            if (reservations.isEmpty()) {
                logger.info("No reservations found for product id: {}", productId);
                return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "No reservations found for the product", null));
            }
            logger.info("Successfully retrieved reservations for product id: {}", productId);
            return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "Successfully retrieved the reservations", reservations));
        } catch (Exception e) {
            logger.error("Error retrieving reservations for product id {}: {}", productId, e.getMessage(), e);
            return ResponseEntity.ok(new ApiResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error finding the reservations", null));
        }
    }

    @GetMapping("/business/{businessId}")
    public ResponseEntity<ApiResponse<List<ReservationForBusinessDTO>>> getReservationsByBusinessId(@PathVariable int businessId) {
        logger.info("Fetching reservations for business ID: {}", businessId);
        try {
            List<ReservationForBusinessDTO> reservations = reservationService.getReservationsByBusinessId(businessId);
            if (reservations.isEmpty()) {
                logger.info("No reservations found for business ID: {}", businessId);
                return ResponseEntity.ok(new ApiResponse<>(true, 200, "No reservations found", null));
            }
            logger.info("Reservations successfully retrieved for business ID: {}", businessId);
            return ResponseEntity.ok(new ApiResponse<>(true, 200, "Reservations retrieved successfully", reservations));
        } catch (Exception e) {
            logger.error("Error retrieving reservations for business ID {}: {}", businessId, e.getMessage());
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "Error retrieving reservations", null));
        }
    }

    @PutMapping("/{reservationId}/status")
    public ApiResponse<Void> updateReservationStatus(@PathVariable int reservationId, @RequestBody ReservationForBusinessDTO reservation) {
        logger.info("Successfully updated reservation {}", reservation);
        try {
            reservationService.updateReservationStatus(reservationId, reservation.getReservationStatus());
            logger.info("Successfully updated reservation {}", reservation);
            return new ApiResponse<>(true, HttpStatus.OK.value(), "Successfully updated the reservation");
        }
        catch(Exception e) {
            logger.error("Error updating reservation {}", e.getMessage(), e);
            return new ApiResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error updating the reservation");
        }
    }

}
