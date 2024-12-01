package com.wandr.backend.dao;

import com.wandr.backend.dto.cart.CartItemDTO;
import com.wandr.backend.entity.CartItem;
import com.wandr.backend.mapper.CartItemRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class CartDAO {

    private final JdbcTemplate jdbcTemplate;

    public CartDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addCartItem(CartItem cartItem) {
        String sql = "INSERT INTO cart_items (traveller_id, product_id, quantity) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, cartItem.getTravellerId(), cartItem.getProductId(), cartItem.getQuantity());
    }

    public void updateCartItemQuantity(long cartItemId, int newQuantity) {
        String sql = "UPDATE cart_items SET quantity = ? WHERE cart_item_id = ?";
        jdbcTemplate.update(sql, newQuantity, cartItemId);
    }

    public CartItem findCartItem(long travellerId, long productId) {
        String sql = "SELECT * FROM cart_items WHERE traveller_id = ? AND product_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new CartItemRowMapper(), travellerId, productId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<CartItem> getCartItemsByTravellerId(Long travellerId) {
        String sql = "SELECT * FROM cart_items WHERE traveller_id = ?";
        return jdbcTemplate.query(sql, new CartItemRowMapper(), travellerId);
    }
    public CartItem findCartItemById(Long cartItemId) {
        String sql = "SELECT * FROM cart_items WHERE cart_item_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new CartItemRowMapper(), cartItemId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void deleteCartItem(Long cartItemId) {
        String sql = "DELETE FROM cart_items WHERE cart_item_id = ?";
        jdbcTemplate.update(sql, cartItemId);
    }


}

