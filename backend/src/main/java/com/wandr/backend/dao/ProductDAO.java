package com.wandr.backend.dao;

import com.wandr.backend.entity.Product;
import com.wandr.backend.mapper.ProductRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public class ProductDAO {

    private final JdbcTemplate jdbcTemplate;

    public ProductDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createProduct(Product product) {
        String sql = "INSERT INTO products (name, description, price, quantity, business_id) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, product.getName(), product.getDescription(), product.getPrice(), product.getQuantity(), product.getBusiness_id());
    }

    public List<Product> getAllProducts() {
        String sql = "SELECT * FROM products";
        return jdbcTemplate.query(sql, new ProductRowMapper());
    }

    public Product findById(Long product_id) {
        String sql = "SELECT * FROM products WHERE product_id =?";
        return jdbcTemplate.queryForObject(sql, new ProductRowMapper(), product_id);

        //TODO : modify the code to handle when the result is null
    }

    public List<Product> findAllByBusinessId(long business_id){
        String sql = "SELECT * FROM products WHERE business_id =?";
        return jdbcTemplate.query(sql, new ProductRowMapper(), business_id);
    }

}
