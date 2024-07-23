package com.wandr.backend.dao;

import com.wandr.backend.dto.business.PopularStoreDTO;
import com.wandr.backend.entity.Business;
import com.wandr.backend.mapper.BusinessRowMapper;
import org.slf4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.slf4j.LoggerFactory;
@Repository
public class BusinessDAO {

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
        String sql = "INSERT INTO businesses (name, email, password, description, services, address, languages, website_url, business_contact, business_type,shop_category, status, owner_name, owner_contact, owner_nic, jwt, salt, created_at, shop_image) VALUES (?, ?, ?, ?, ?::jsonb, ?, ?::jsonb, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)";
        jdbcTemplate.update(sql, business.getName(), business.getEmail(), business.getPassword(), business.getDescription(), business.getServices().toString(), business.getAddress(), business.getLanguages().toString(), business.getWebsiteUrl(), business.getBusinessContact(), business.getBusinessType(), business.getShopCategory(), business.getStatus(),business.getOwnerName(), business.getOwnerContact(), business.getOwnerNic(), business.getJwt(), business.getSalt(), business.getCreatedAt(), business.getShopImage());
    }

    public Business findById(Long businessId) {
        String sql = "SELECT * FROM businesses WHERE business_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{businessId}, new BusinessRowMapper());
    }

    public void updateProfile(Business business) {
        String sql = "UPDATE businesses SET name = ?, email = ?, description = ?, services = ?::jsonb, address = ?, languages =?::jsonb, website_url = ?, business_contact = ?, shop_image = ?, business_type = ?, shop_category = ?, owner_name = ?, owner_contact = ?, owner_nic = ?, status = ?, plan_id=? WHERE business_id = ?";
        logger.info("sql",sql);
        jdbcTemplate.update(sql, business.getName(), business.getEmail(), business.getDescription(), business.getServices(), business.getAddress(), business.getLanguages(), business.getWebsiteUrl(), business.getBusinessContact(), business.getShopImage(), business.getBusinessType(), business.getShopCategory(), business.getOwnerName(), business.getOwnerContact(), business.getOwnerNic(), business.getStatus(), business.getPlanId(), business.getBusinessId());
    }


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


}
