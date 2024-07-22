package com.wandr.backend.dao;

import com.wandr.backend.dto.cart.CartItemDTO;
import com.wandr.backend.entity.CartItem;
import com.wandr.backend.mapper.CartItemRowMapper;
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

    public void addItemToCart(int cartId, int productId, int unitId, int quantity) {
        String sql = "INSERT INTO cart_items (cart_id, product_id, unit_id, quantity) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, cartId, productId, unitId, quantity);
    }

    public List<CartItem> getCartItems(int cartId) {
        String sql = "SELECT ci.cart_item_id, p.name, p.price, ci.quantity " +
                "FROM cart_items ci " +
                "JOIN products p ON ci.product_id = p.product_id " +
                "WHERE ci.cart_id = ?";
        return jdbcTemplate.query(sql, new Object[]{cartId}, new CartItemRowMapper());
    }

    public BigDecimal getTotalPrice(int cartId) {
        String sql = "SELECT SUM(p.price * ci.quantity) AS total_price " +
                "FROM cart_items ci " +
                "JOIN products p ON ci.product_id = p.product_id " +
                "WHERE ci.cart_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{cartId}, BigDecimal.class);
    }

    public int createReservation(int travellerId, BigDecimal totalPrice) {
        String sql = "INSERT INTO reservations (traveller_id, reservation_date, expiration_date, advance_payment) " +
                "VALUES (?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + interval '7 days', ?) RETURNING reservation_id";
        return jdbcTemplate.queryForObject(sql, new Object[]{travellerId, totalPrice}, Integer.class);
    }

    public void reserveProductUnits(int reservationId, int cartId) {
        String updateUnitsSql = "UPDATE product_units " +
                "SET reservation_status = 'reserved', reservation_id = ? " +
                "WHERE unit_id IN (SELECT unit_id FROM cart_items WHERE cart_id = ?)";
        jdbcTemplate.update(updateUnitsSql, reservationId, cartId);

        String clearCartSql = "DELETE FROM cart_items WHERE cart_id = ? AND unit_id IN " +
                "(SELECT unit_id FROM product_units WHERE reservation_id = ?)";
        jdbcTemplate.update(clearCartSql, cartId, reservationId);
    }
}
