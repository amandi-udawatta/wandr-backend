package com.wandr.backend.dao;

import com.wandr.backend.entity.Product;
import com.wandr.backend.entity.Traveller;
import com.wandr.backend.mapper.ProductRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

@Repository
public class ProductDAO {

    private final JdbcTemplate jdbcTemplate;

    public ProductDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final Logger logger = LoggerFactory.getLogger(ProductDAO.class);

    public void createProduct(Product product) {
        String sql = "INSERT INTO products (name, description, price, quantity, business_id, reservation_payment, image) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, product.getName(), product.getDescription(), product.getPrice(), product.getQuantity(), product.getBusiness_id(), product.getReservation_payment(), product.getImage());
    }

    public List<Product> getAllProducts() {
        String sql = "SELECT * FROM products";
        try {
            return jdbcTemplate.query(sql, new ProductRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Product findById(Long product_id) {
        String sql = "SELECT * FROM products WHERE product_id =?";
        try {
            return jdbcTemplate.queryForObject(sql, new ProductRowMapper(), product_id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Product> findAllByBusinessId(long business_id){
        String sql = "SELECT * FROM products WHERE business_id =?";
        try {
            return jdbcTemplate.query(sql, new ProductRowMapper(), business_id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void updateProduct(Product product) {
        String sql = "UPDATE products SET name = ?, description = ?, price = ?, quantity = ?, reservation_payment = ?, image = ?  WHERE product_id = ?";
        jdbcTemplate.update(sql, product.getName(), product.getDescription(), product.getPrice(), product.getQuantity(), product.getReservation_payment(), product.getImage(), product.getProduct_id());
    }

    public void updateProductQuantity(long product_id, int quantity) {
        String sql = "UPDATE products SET quantity = ? WHERE product_id = ?";
        jdbcTemplate.update(sql, quantity, product_id);
    }

    public void deleteProduct(long product_id) {
        String sql = "DELETE FROM products WHERE product_id = ?";
        jdbcTemplate.update(sql, product_id);
    }



}
