package com.wandr.backend.mapper;

import com.wandr.backend.entity.Product;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductRowMapper implements RowMapper<Product> {

    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        Product product = new Product();
        product.setProduct_id(rs.getLong("product_id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setImage(rs.getString("image"));
        product.setQuantity(rs.getInt("quantity"));
        product.setBusiness_id(rs.getLong("business_id"));
        product.setSales_count(rs.getInt("sales_count"));
        product.setReservation_payment(rs.getBigDecimal("reservation_payment"));
        return product;
    }
}
