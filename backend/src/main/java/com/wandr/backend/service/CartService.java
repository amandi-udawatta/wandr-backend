package com.wandr.backend.service;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.cart.CartItemDTO;
import com.wandr.backend.dto.statistics.CountryStatisticsDTO;
import com.wandr.backend.dto.statistics.RevenueDTO;
import com.wandr.backend.dto.statistics.StatisticsDTO;

import java.util.List;
import java.math.BigDecimal;


public interface CartService {

    ApiResponse<Void> addItemToCart(int cartId, int productId, int unitId, int quantity);
    public ApiResponse<List<CartItemDTO>> getCartItems(int cartId);
    ApiResponse<BigDecimal> getTotalPrice(int cartId);
    ApiResponse<Integer> createReservation(int travellerId, int cartId);

}
