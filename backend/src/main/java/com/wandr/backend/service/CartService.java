package com.wandr.backend.service;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.cart.CartItemDTO;

import java.util.List;
import java.math.BigDecimal;


public interface CartService {

    ApiResponse<Void> addItemToCart(CartItemDTO cartItemDTO);

    ApiResponse<List<CartItemDTO>> viewCart(Long travellerId);
    ApiResponse<Void> deleteCartItem(Long cartItemId);



}
