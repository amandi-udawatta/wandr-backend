package com.wandr.backend.mapper;

import com.wandr.backend.entity.Admin;
import com.wandr.backend.entity.Traveller;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AdminRowMapper implements RowMapper<Admin> {

    @Override
    public Admin mapRow(ResultSet rs, int rowNum) throws SQLException {
        Admin admin = new Admin();
        admin.setAdminId(rs.getLong("admin_id"));
        admin.setName(rs.getString("name"));
        admin.setEmail(rs.getString("email"));
        admin.setPassword(rs.getString("password"));
        admin.setJwt(rs.getString("jwt"));
        admin.setSalt(rs.getString("salt"));



        return admin;
    }
}
