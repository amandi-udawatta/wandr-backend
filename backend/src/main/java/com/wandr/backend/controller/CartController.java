package com.wandr.backend.controller;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.cart.CartItemDTO;
import com.wandr.backend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Void>> addItemToCart(@RequestParam int cartId, @RequestParam int productId, @RequestParam int unitId, @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.addItemToCart(cartId, productId, unitId, quantity));
    }

    @GetMapping("/items")
    public ResponseEntity<ApiResponse<List<CartItemDTO>>> getCartItems(@RequestParam int cartId) {
        return ResponseEntity.ok(cartService.getCartItems(cartId));
    }

    @GetMapping("/total-price")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalPrice(@RequestParam int cartId) {
        return ResponseEntity.ok(cartService.getTotalPrice(cartId));
    }

    @PostMapping("/reserve")
    public ResponseEntity<ApiResponse<Integer>> createReservation(@RequestParam int travellerId, @RequestParam int cartId) {
        return ResponseEntity.ok(cartService.createReservation(travellerId, cartId));
    }
}
