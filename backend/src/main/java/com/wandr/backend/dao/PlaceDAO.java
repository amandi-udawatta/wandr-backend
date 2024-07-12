package com.wandr.backend.dao;

import com.wandr.backend.entity.Places;
import com.wandr.backend.mapper.PlaceRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PlaceDAO {

    private final JdbcTemplate jdbcTemplate;

    public PlaceDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Places place) {
        String sql = "INSERT INTO places (name, description, latitude, longitude, address) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, place.getName(), place.getDescription(), place.getLatitude(), place.getLongitude(), place.getAddress());
    }

    public List<Places> findAll() {
        String sql = "SELECT * FROM places";
        return jdbcTemplate.query(sql, new PlaceRowMapper());
    }
}
