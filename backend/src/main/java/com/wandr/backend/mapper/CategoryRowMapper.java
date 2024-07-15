package com.wandr.backend.mapper;

import com.wandr.backend.entity.Category;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryRowMapper implements RowMapper<Category> {

    @Override
    public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
        Category category = new Category();
        category.setId(rs.getLong("category_id"));
        category.setName(rs.getString("name"));
        return category;
    }
}
