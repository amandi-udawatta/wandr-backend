package com.wandr.backend.service.impl;

import com.wandr.backend.dao.CartDAO;
import com.wandr.backend.dao.ProductDAO;
import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.cart.CartItemDTO;
import com.wandr.backend.entity.CartItem;
import com.wandr.backend.entity.Product;
import com.wandr.backend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);

    @Autowired
    public CartServiceImpl(CartDAO cartDAO, ProductDAO productDAO) {
        this.productDAO = productDAO;
        this.cartDAO = cartDAO;
    }

    @Override
    public ApiResponse<List<CartItemDTO>> viewCart(Long travellerId) {
        // Retrieve cart items from the database
        List<CartItem> cartItems = cartDAO.getCartItemsByTravellerId(travellerId);
        if (cartItems.isEmpty()) {
            return new ApiResponse<>(false, 404, "Cart is empty", null);
        }
        // Map cart items to DTOs, including product details
        List<CartItemDTO> cartItemDTOs = cartItems.stream().map(cartItem -> {
            Product product = productDAO.findById(cartItem.getProductId());
            CartItemDTO dto = new CartItemDTO();
            dto.setTravellerId(cartItem.getTravellerId());
            dto.setProductId(cartItem.getProductId());
            dto.setQuantity(cartItem.getQuantity());
            dto.setProductName(product.getName());
            dto.setUnitPrice(product.getPrice());
            dto.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            return dto;
        }).collect(Collectors.toList());

        return new ApiResponse<>(true, 200, "Cart retrieved successfully", cartItemDTOs);
    }


    @Override
    public ApiResponse<Void> addItemToCart(CartItemDTO cartItemDTO) {

        // Validate quantity
        if (cartItemDTO.getQuantity() <= 0) {
            logger.info("Quantity must be greater than zero");
            return new ApiResponse<>(false, 400, "Quantity must be greater than zero");
        }

        // Validate product existence and stock
        Product product = productDAO.findById(cartItemDTO.getProductId());

        if (product == null) {
            return new ApiResponse<>(false, 404, "Product not found");
        }
        if (product.getQuantity() < cartItemDTO.getQuantity()) {
            return new ApiResponse<>(false, 400, "Insufficient stock for the product");
        }

        // Check if the cart item already exists for the traveller and product
        CartItem existingCartItem = cartDAO.findCartItem(cartItemDTO.getTravellerId(), cartItemDTO.getProductId());

        if (existingCartItem != null) {
            // Update the existing cart item quantity
            cartDAO.updateCartItemQuantity(
                    existingCartItem.getCartItemId(),
                    existingCartItem.getQuantity() + cartItemDTO.getQuantity()
            );
        } else {
            // Add a new item to the cart
            CartItem newCartItem = new CartItem();
            newCartItem.setTravellerId(cartItemDTO.getTravellerId());
            newCartItem.setProductId(cartItemDTO.getProductId());
            newCartItem.setQuantity(cartItemDTO.getQuantity());
            cartDAO.addCartItem(newCartItem);
        }

        return new ApiResponse<>(true, 200, "Product added to cart successfully");
    }

    @Override
    public ApiResponse<Void> deleteCartItem(Long cartItemId) {
        // Check if the cart item exists
        CartItem cartItem = cartDAO.findCartItemById(cartItemId);
        if (cartItem == null) {
            return new ApiResponse<>(false, 404, "Cart item not found");
        }
        // Delete the item
        cartDAO.deleteCartItem(cartItemId);

        return new ApiResponse<>(true, 200, "Cart item deleted successfully");
    }



}


//TODO: Implement the ADD to card, delete, view cart