package com.wandr.backend.mapper;

import com.wandr.backend.entity.Business;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BusinessRowMapper implements RowMapper<Business> {

    @Override
    public Business mapRow(ResultSet rs, int rowNum) throws SQLException {
        Business business = new Business();
        business.setBusinessId(rs.getLong("business_id"));
        business.setName(rs.getString("name"));
        business.setEmail(rs.getString("email"));
        business.setPassword(rs.getString("password"));
        business.setDescription(rs.getString("description"));
        business.setAddress(rs.getString("address"));
        business.setWebsiteUrl(rs.getString("website_url"));
        business.setShopImage(rs.getString("shop_image"));
        business.setBusinessContact(rs.getString("business_contact"));
        business.setBusinessType(rs.getInt("business_type"));
        business.setShopCategory(rs.getInt("shop_category"));
        business.setStatus(rs.getString("status"));
        business.setOwnerName(rs.getString("owner_name"));
        business.setOwnerContact(rs.getString("owner_contact"));
        business.setOwnerNic(rs.getString("owner_nic"));
        business.setJwt(rs.getString("jwt"));
        business.setSalt(rs.getString("salt"));
        business.setCreatedAt(rs.getTimestamp("created_at"));
        business.setShopCategory(rs.getInt("shop_category"));
        business.setPlanId(rs.getInt("plan_id"));


        String services = rs.getString("services");
        if (services != null && !services.trim().isEmpty()) {
            List<String> serviceList = Arrays.stream(services.replaceAll("[\\[\\]\"]", "").split(","))
                    .filter(str -> !str.isEmpty()) // Add this to filter out empty strings
                    .collect(Collectors.toList());
            business.setServices(serviceList);
        } else {
            business.setServices(Collections.emptyList());
        }

        String languages = rs.getString("languages");
        if (languages != null && !languages.trim().isEmpty()) {
            List<String> languageList = Arrays.stream(languages.replaceAll("[\\[\\]\"]", "").split(","))
                    .filter(str -> !str.isEmpty()) // Add this to filter out empty strings
                    .collect(Collectors.toList());
            business.setLanguages(languageList);
        } else {
            business.setLanguages(Collections.emptyList());
        }

        return business;
    }
}
