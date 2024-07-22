package com.wandr.backend.service.impl;

import com.wandr.backend.dao.CartDAO;
import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.cart.CartItemDTO;
import com.wandr.backend.entity.CartItem;
import com.wandr.backend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartDAO cartDAO;

    @Autowired
    public CartServiceImpl(CartDAO cartDAO) {
        this.cartDAO = cartDAO;
    }

    @Override
    public ApiResponse<Void> addItemToCart(int cartId, int productId, int unitId, int quantity) {
        cartDAO.addItemToCart(cartId, productId, unitId, quantity);
        return new ApiResponse<>(true, 200, "Item added to cart successfully");
    }

    @Override
    public ApiResponse<List<CartItemDTO>> getCartItems(int cartId) {
        List<CartItem> items = cartDAO.getCartItems(cartId);
        List<CartItemDTO> itemsDTO = items.stream().map(this::cartItemToCartItemDTO).toList();
        return new ApiResponse<>(true, 200, "Cart items retrieved successfully", itemsDTO);
    }

    @Override
    public ApiResponse<BigDecimal> getTotalPrice(int cartId) {
        BigDecimal totalPrice = cartDAO.getTotalPrice(cartId);
        return new ApiResponse<>(true, 200, "Total price calculated successfully", totalPrice);
    }

    @Override
    public ApiResponse<Integer> createReservation(int travellerId, int cartId) {
        BigDecimal totalPrice = cartDAO.getTotalPrice(cartId);
        int reservationId = cartDAO.createReservation(travellerId, totalPrice);
        cartDAO.reserveProductUnits(reservationId, cartId);
        return new ApiResponse<>(true, 200, "Reservation created successfully", reservationId);
    }


    private CartItemDTO cartItemToCartItemDTO(CartItem cartItem) {
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setCartItemId(cartItem.getCartItemId());
        cartItemDTO.setCartId(cartItem.getCartId());
        cartItemDTO.setProductId(cartItem.getProductId());
        cartItemDTO.setUnitId(cartItem.getUnitId());
        cartItemDTO.setAddedAt(cartItem.getAddedAt());
        cartItemDTO.setQuantity(cartItem.getQuantity());
        return cartItemDTO;
    }

}
