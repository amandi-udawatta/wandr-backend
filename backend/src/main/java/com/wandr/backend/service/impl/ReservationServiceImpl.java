package com.wandr.backend.service.impl;

import com.wandr.backend.dao.CartDAO;
import com.wandr.backend.dao.ProductDAO;
import com.wandr.backend.dao.ReservationDAO;
import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.reservation.CreateReservationRequestDTO;
import com.wandr.backend.entity.CartItem;
import com.wandr.backend.entity.Product;
import com.wandr.backend.entity.Reservation;
import com.wandr.backend.entity.ReservedUnit;
import com.wandr.backend.service.ReservationService;
import com.wandr.backend.dto.reservation.ReservationForBusinessDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationDAO reservationDAO;
    private final CartDAO cartDAO;
    private final ProductDAO productDAO;
    private final Logger logger = LoggerFactory.getLogger(ReservationServiceImpl.class);

    @Autowired
    public ReservationServiceImpl(CartDAO cartDAO, ProductDAO productDAO, ReservationDAO reservationDAO) {
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
        this.reservationDAO = reservationDAO;
    }

    @Override
    public ApiResponse<Void> createReservation(CreateReservationRequestDTO requestDTO) {
        logger.info("Creating reservation for traveller ID: {}", requestDTO.getTravellerId());
        Long travellerId = requestDTO.getTravellerId();
        List<Long> cartItemIds = requestDTO.getCartItemIds();

        // Fetch cart items
        List<CartItem> cartItems = cartDAO.getCartItemsByIds(cartItemIds);

        // Validate cart items belong to the traveller
        if (cartItems.stream().anyMatch(cartItem -> !cartItem.getTravellerId().equals(travellerId))) {
            return new ApiResponse<>(false, 403, "Some cart items do not belong to the traveller");
        }

        // Check stock availability and calculate total reservation amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            Product product = productDAO.findById(cartItem.getProductId());
            if (product == null || product.getQuantity() < cartItem.getQuantity()) {
                logger.error("Insufficient stock for product: {}", cartItem.getProductId());
                return new ApiResponse<>(false, 400, "Insufficient stock for product: " + cartItem.getProductId());
            }
            totalAmount = totalAmount.add(product.getReservation_payment().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        // Create reservation
        Reservation reservation = new Reservation();
        reservation.setTravellerId(travellerId);
        reservation.setReservationDate(new Timestamp(System.currentTimeMillis()));
        reservation.setExpirationDate(new Timestamp(System.currentTimeMillis() + 14 * 24 * 60 * 60 * 1000)); // 2 weeks later
        reservation.setTotalAmount(totalAmount);
        Long reservationId = reservationDAO.createReservation(reservation);

        // Create reserved units and update product quantities
        for (CartItem cartItem : cartItems) {
            Product product = productDAO.findById(cartItem.getProductId());

            ReservedUnit reservedUnit = new ReservedUnit();
            reservedUnit.setProductId(cartItem.getProductId());
            reservedUnit.setReservationId(reservationId);
            reservedUnit.setQuantity(cartItem.getQuantity());
            reservedUnit.setReservationStatus("Active");
            reservationDAO.createReservedUnit(reservedUnit);

            productDAO.updateProductQuantity(product.getProduct_id(), product.getQuantity() - cartItem.getQuantity());
        }

        // Remove cart items
        cartDAO.deleteCartItems(cartItemIds);

        return new ApiResponse<>(true, 200, "Reservation created successfully");
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
    @Override
    public List<ReservationForBusinessDTO> getReservationsByProductId(int productId) {
        return reservationDAO.findReservationsByProductId(productId);
    }

    @Override
    public boolean updateReservationStatus(long reservationUnitId, String status) {
        System.out.println("status" + status);
        return reservationDAO.updateReservationStatus(reservationUnitId, status);
    }

    @Override
    public List<ReservationForBusinessDTO> getReservationsByBusinessId(int businessId) {
        return reservationDAO.findReservationsByBusinessId(businessId);
    }

    @Override
    public List<ReservationForBusinessDTO> getReservedItemsByTravellerId(Long travellerId) {
        return reservationDAO.findReservedItemsByTravellerId(travellerId);
    }

    @Override
    public List<ReservationForBusinessDTO> getPurchasedItemsByTravellerId(Long travellerId) {
        return reservationDAO.findPurchasedItemsByTravellerId(travellerId);
    }


}


//TODO: Create reservation method, reservations per traveller, purchased items per traveller
