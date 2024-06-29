package com.wandr.backend.dao;

import com.wandr.backend.entity.Admin;
import com.wandr.backend.entity.Traveller;
import com.wandr.backend.mapper.AdminRowMapper;
import com.wandr.backend.mapper.TravellerRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AdminDAO {

    private final JdbcTemplate jdbcTemplate;

    public AdminDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM admins WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{email}, Integer.class);
        return count != null && count > 0;
    }
    public Admin findById(Long adminId) {
        String sql = "SELECT * FROM admins WHERE admin_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{adminId}, new AdminRowMapper());
    }

    public Optional<Admin> findByEmail(String email) {
        String sql = "SELECT * FROM admins WHERE email = ?";
        List<Admin> admin = jdbcTemplate.query(sql, new Object[]{email}, new AdminRowMapper());
        return admin.isEmpty() ? Optional.empty() : Optional.of(admin.get(0));
    }

    public void updateAdminJwt(String jwt, Long adminId) {
        String sql = "UPDATE admins SET jwt = ? WHERE admin_id = ?";
        jdbcTemplate.update(sql, jwt, adminId);
    }
}
