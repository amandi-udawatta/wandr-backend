package com.wandr.backend.dao;

import com.wandr.backend.entity.Traveller;
import com.wandr.backend.mapper.TravellerRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TravellerDAO {

    private final JdbcTemplate jdbcTemplate;

    public TravellerDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM travellers WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{email}, Integer.class);
        return count != null && count > 0;
    }

    public void save(Traveller traveller) {
        String sql = "INSERT INTO travellers (name, email, password, country, categories, activities) VALUES (?, ?, ?, ?, ?::jsonb, ?::jsonb)";
        jdbcTemplate.update(sql, traveller.getName(), traveller.getEmail(), traveller.getPassword(), traveller.getCountry(), traveller.getCategories().toString(), traveller.getActivities().toString());
    }

    public void updateCategories(Long travellerId, List<Integer> categories) {
        String sql = "UPDATE travellers SET categories = ?::jsonb WHERE traveller_id = ?";
        jdbcTemplate.update(sql, categories.toString(), travellerId);
    }

    public void updateActivities(Long travellerId, List<Integer> activities) {
        String sql = "UPDATE travellers SET activities = ?::jsonb WHERE traveller_id = ?";
        jdbcTemplate.update(sql, activities.toString(), travellerId);
    }
    public Traveller findById(Long travellerId) {
        String sql = "SELECT * FROM travellers WHERE traveller_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{travellerId}, new TravellerRowMapper());
    }

    public void updateProfile(Traveller traveller) {
        String sql = "UPDATE travellers SET name = ?, country = ?, categories = ?::jsonb, activities = ?::jsonb, profile_image = ? WHERE traveller_id = ?";
        jdbcTemplate.update(sql, traveller.getName(), traveller.getCountry(), traveller.getCategories().toString(), traveller.getActivities().toString(), traveller.getProfileImage(), traveller.getTravellerId());
    }

    public Optional<Traveller> findByEmail(String email) {
        String sql = "SELECT * FROM travellers WHERE email = ?";
        List<Traveller> travellers = jdbcTemplate.query(sql, new Object[]{email}, new TravellerRowMapper());
        return travellers.isEmpty() ? Optional.empty() : Optional.of(travellers.get(0));
    }
}
