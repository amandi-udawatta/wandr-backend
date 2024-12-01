package com.wandr.backend.mapper;

import com.wandr.backend.entity.Activity;
import com.wandr.backend.entity.CartItem;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CartItemRowMapper implements RowMapper<CartItem> {
    @Override
    public CartItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        CartItem cartItem = new CartItem();
        cartItem.setCartItemId(rs.getLong("cart_item_id"));
        cartItem.setTravellerId(rs.getLong("traveller_id"));
        cartItem.setProductId(rs.getLong("product_id"));
        cartItem.setQuantity(rs.getInt("quantity"));
        return cartItem;
    }
}

