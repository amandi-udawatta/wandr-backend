package com.wandr.backend.controller;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.cart.CartItemDTO;
import com.wandr.backend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{travellerId}")
    public ResponseEntity<ApiResponse<List<CartItemDTO>>> viewCart(@PathVariable Long travellerId) {
        try {
            ApiResponse<List<CartItemDTO>> response = cartService.viewCart(travellerId);
            logger.info("Successfully viewed cart");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error viewing cart", e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "Error viewing cart", null));
        }
    }
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Void>> addItemToCart(@RequestBody CartItemDTO cartItemDTO) {
        try {
            ApiResponse<Void> response = cartService.addItemToCart(cartItemDTO);
            logger.info("Successfully added item to cart");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error adding item to cart", e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "Error adding item to cart", null));
        }
    }

    @DeleteMapping("/delete/{cartItemId}")
    public ResponseEntity<ApiResponse<Void>> deleteCartItem(@PathVariable Long cartItemId) {
        try {
            ApiResponse<Void> response = cartService.deleteCartItem(cartItemId);
            logger.info("Successfully deleted item from cart");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error deleting item from cart", e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "Error deleting item from cart", null));
        }
    }






}
