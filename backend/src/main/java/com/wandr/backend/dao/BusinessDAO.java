package com.wandr.backend.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wandr.backend.dto.business.PaidBusinessDTO;
import com.wandr.backend.dto.business.PopularStoreDTO;
import com.wandr.backend.entity.Activity;
import com.wandr.backend.entity.Business;
import com.wandr.backend.entity.Category;
import com.wandr.backend.mapper.BusinessRowMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
@Repository
public class BusinessDAO {

    @Value("${core.backend.url}")
    private String backendUrl;

    private final JdbcTemplate jdbcTemplate;
    Logger logger = LoggerFactory.getLogger(BusinessDAO.class);

    public BusinessDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM businesses WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{email}, Integer.class);
        return count != null && count > 0;
    }



    //    public class BusinessSignupDTO {
//        private String name;
//        private String email;
//        private String password;
//        private String description;
//        private String services;
//        private String address;
//        private String languages;
//        private String websiteUrl;
//        private String businessContact;
//        private String shopImage;
//        private Integer businessType;
//        private Integer shopCategory;
//        private String ownerName;
//        private String ownerContact;
//        private String ownerNic;
//        private String jwt;
//        private String salt;
//
//    }

    public void save(Business business) {
        String sql = "INSERT INTO businesses (name, email, password, description, services, address,latitude,longitude, languages, website_url, business_contact, business_type,shop_category, status, owner_name, owner_contact, owner_nic, jwt, salt, created_at, shop_image) VALUES (?, ?, ?, ?, ?::jsonb, ?,?,?, ?::jsonb, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)";
        try {
            // Convert the services and languages lists to JSON strings
            String servicesJson = new ObjectMapper().writeValueAsString(business.getServices());
            String languagesJson = new ObjectMapper().writeValueAsString(business.getLanguages());
            jdbcTemplate.update(sql, business.getName(), business.getEmail(), business.getPassword(), business.getDescription(), servicesJson, business.getAddress(),business.getLatitude(),business.getLongitude(), languagesJson, business.getWebsiteUrl(), business.getBusinessContact(), business.getBusinessType(), business.getShopCategory(), business.getStatus(),business.getOwnerName(), business.getOwnerContact(), business.getOwnerNic(), business.getJwt(), business.getSalt(), business.getCreatedAt(), business.getShopImage());
        } catch (JsonProcessingException e) {
            logger.error("Error converting services or languages to JSON", e);
            throw new RuntimeException("Failed to save business due to JSON processing error", e);
        }
    }

    public Business findById(Long businessId) {
        String sql = "SELECT * FROM businesses WHERE business_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{businessId}, new BusinessRowMapper());
    }

    public void updateProfile(Business business) {
        String sql = "UPDATE businesses SET name = ?, email = ?, description = ?, services = ?::jsonb, address = ?, languages = ?::jsonb, website_url = ?, business_contact = ?, shop_image = ?, profile_image = ?, business_type = ?, shop_category = ?, owner_name = ?, owner_contact = ?, owner_nic = ?, status = ?, plan_id=?, latitude=?, longitude=? WHERE business_id = ?";
        try {
            // Convert the services and languages lists to JSON strings
            String servicesJson = new ObjectMapper().writeValueAsString(business.getServices());
            String languagesJson = new ObjectMapper().writeValueAsString(business.getLanguages());
        jdbcTemplate.update(sql, business.getName(), business.getEmail(), business.getDescription(), servicesJson, business.getAddress(), languagesJson, business.getWebsiteUrl(), business.getBusinessContact(), business.getShopImage(),business.getProfileImage(), business.getBusinessType(), business.getShopCategory(), business.getOwnerName(), business.getOwnerContact(), business.getOwnerNic(), business.getStatus(), business.getPlanId(), business.getLatitude(), business.getLongitude(), business.getBusinessId());
        } catch (JsonProcessingException e) {
            logger.error("Error converting services or languages to JSON", e);
            throw new RuntimeException("Failed to update profile due to JSON processing error", e);
        }}


    public Optional<Business> findByEmail(String email) {
        String sql = "SELECT * FROM businesses WHERE email = ?";
        List<Business> businesses = jdbcTemplate.query(sql, new Object[]{email}, new BusinessRowMapper());
        return businesses.isEmpty() ? Optional.empty() : Optional.of(businesses.get(0));
    }

    public void updateBusinessJwt(String jwt, Long businessId) {
        String sql = "UPDATE businesses SET jwt = ? WHERE business_id = ?";
        jdbcTemplate.update(sql, jwt, businessId);
    }



    //get all pending businesses
    public List<Business> getPendingBusinesses() {
        String sql = "SELECT * FROM businesses WHERE status = 'pending'";
        return jdbcTemplate.query(sql, new BusinessRowMapper());
    }

    public List<Business> getApprovedBusinesses() {
        String sql = "SELECT * FROM businesses WHERE status = 'approved'";
        return jdbcTemplate.query(sql, new BusinessRowMapper());
    }

    //get business name by id
    public String getBusinessNameById(Long businessId) {
        String sql = "SELECT name FROM businesses WHERE business_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{businessId}, String.class);
    }

    //get top 3 popular stores
    public List<PopularStoreDTO> getPopularStores() {
        String sql = "SELECT b.name AS shop_name, p.total_sales_count AS sales_count, " +
                "ROUND((p.total_sales_count::numeric / total.total_sales) * 100, 2) AS popularity " +
                "FROM businesses b " +
                "JOIN (SELECT business_id, SUM(sales_count) AS total_sales_count FROM products GROUP BY business_id) p " +
                "ON b.business_id = p.business_id " +
                "JOIN (SELECT SUM(sales_count) AS total_sales FROM products) total ON true " +
                "ORDER BY p.total_sales_count DESC " +
                "LIMIT 3;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new PopularStoreDTO(
                rs.getString("shop_name"),
                rs.getInt("sales_count"),
                rs.getDouble("popularity")
        ));
    }

    // get paid businesses
    public List<PaidBusinessDTO> getPaidBusinesses() {
        String sql = "SELECT b.*, bp.price AS payment_amount, bp.name as plan, EXTRACT(DAY FROM (b.plan_end_date - CURRENT_TIMESTAMP)) AS remaining_days " +
                "FROM businesses b " +
                "JOIN business_plan bp ON b.plan_id = bp.plan_id " +
                "WHERE b.status = 'paid'";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Business business = new BusinessRowMapper().mapRow(rs, rowNum);
            String businessType = business.getBusinessType() == 1 ? "Shop" : "Service";
            String shopCategory = jdbcTemplate.queryForObject("SELECT name FROM shop_categories WHERE category_id = ?", new Object[]{business.getShopCategory()}, String.class);
            String shopImage = business.getShopImage() == null ? null : backendUrl + "/business/shop_images/" + business.getShopImage();
            String profileImage = business.getProfileImage() == null ? null : backendUrl + "/business/profile_images/" + business.getProfileImage();

            return new PaidBusinessDTO(
                    business.getBusinessId(),
                    business.getName(),
                    business.getEmail(),
                    business.getDescription(),
                    business.getServices(),
                    business.getAddress(),
                    business.getLanguages(),
                    business.getWebsiteUrl(),
                    business.getBusinessContact(),
                    shopImage,
                    profileImage,
                    businessType,
                    business.getOwnerName(),
                    business.getOwnerContact(),
                    business.getOwnerNic(),
                    business.getCreatedAt(),
                    shopCategory,
                    rs.getString("plan"),
                    business.getStatus(),
                    rs.getBigDecimal("payment_amount"),
                    rs.getTimestamp("paid_date"),
                    rs.getTimestamp("plan_end_date"),
                    rs.getInt("remaining_days"),
                    business.getRating()
            );
        });
    }

    public void setStatus(Long businessId, String status) {
        String sql = "UPDATE businesses SET status = ? WHERE business_id = ?";
        jdbcTemplate.update(sql, status, businessId);
    }

    //logout business
    public void deleteBusinessJwt(Long businessId) {
        String sql = "UPDATE businesses SET jwt = NULL WHERE business_id = ?";
        jdbcTemplate.update(sql, businessId);
    }

    //rate business
    public void rateBusiness(Long businessId, Integer rating) {
        String sql = "UPDATE businesses SET rating = ? WHERE business_id = ?";
        jdbcTemplate.update(sql, rating, businessId);
    }


}
