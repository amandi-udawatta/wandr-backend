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
        cartItem.setCartId(rs.getLong("cart_id"));
        cartItem.setProductId(rs.getLong("product_id"));
        cartItem.setUnitId(rs.getLong("unit_id"));
        cartItem.setAddedAt(rs.getTimestamp("added_at"));
        cartItem.setQuantity(rs.getInt("quantity"));
        return cartItem;
    }
}
