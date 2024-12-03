package com.wandr.backend.dao;

import com.wandr.backend.dto.business.BusinessStatisticsDTO;
import com.wandr.backend.dto.business.OngoingAdvertisementDTO;
import com.wandr.backend.dto.business.TopProductDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Repository
public class BusinessStatDAO {

    private final JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(BusinessStatDAO.class);

    public BusinessStatDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public BusinessStatisticsDTO getBusinessStatistics(Long businessId) {
        BusinessStatisticsDTO statistics = new BusinessStatisticsDTO();
        // Total reserved products
        statistics.setTotalReservedProducts(getTotalReservedProducts(businessId));
        logger.info("Total reserved products: " + getTotalReservedProducts(businessId));
        // Total revenue
        statistics.setTotalRevenue(getTotalRevenue(businessId));
        logger.info("Total revenue: " + getTotalRevenue(businessId));
        // Positive feedbacks
        statistics.setPositiveFeedbacks(getPositiveFeedbacks(businessId));
        logger.info("Positive feedbacks: " + getPositiveFeedbacks(businessId));
        // Negative feedbacks
        statistics.setNegativeFeedbacks(getNegativeFeedbacks(businessId));
        logger.info("Negative feedbacks: " + getNegativeFeedbacks(businessId));
        // Remaining days to end plan
        statistics.setRemainingDaysToEndPlan(getRemainingDaysToEndPlan(businessId));
        logger.info("Remaining days to end plan: " + getRemainingDaysToEndPlan(businessId));

        return statistics;
    }

    private int getTotalReservedProducts(Long businessId) {
        try {
            String sql = """
        SELECT COALESCE(SUM(ru.quantity), 0) AS total_reserved_products
        FROM reserved_units ru
        JOIN products p ON ru.product_id = p.product_id
        WHERE p.business_id = ?
    """;
        return jdbcTemplate.queryForObject(sql, Integer.class, businessId);
        } catch (Exception e) {
            logger.error("Error getting total reserved products: " + e.getMessage());
            return 0;
        }
    }

    private double getTotalRevenue(Long businessId) {
        try {
        String sql = """
        SELECT COALESCE(SUM(ru.quantity * p.reservation_payment), 0.0) AS total_revenue
        FROM reserved_units ru
        JOIN products p ON ru.product_id = p.product_id
        WHERE p.business_id = ?
    """;
        return jdbcTemplate.queryForObject(sql, Double.class, businessId);
        } catch (Exception e) {
            logger.error("Error getting total revenue: " + e.getMessage());
            return 0.0;
        }
    }

    private int getPositiveFeedbacks(Long businessId) {
        try {
            String sql = """
        SELECT COALESCE(SUM(CASE WHEN b.rating >= 3 THEN 1 ELSE 0 END), 0) AS positive_feedbacks
        FROM businesses b
        WHERE b.business_id = ?
    """;
        return jdbcTemplate.queryForObject(sql, Integer.class, businessId);
        } catch (Exception e) {
            logger.error("Error getting positive feedbacks: " + e.getMessage());
            return 0;
        }
    }

    private int getNegativeFeedbacks(Long businessId) {
        try {
        String sql = """
        SELECT COALESCE(SUM(CASE WHEN b.rating < 3 THEN 1 ELSE 0 END), 0) AS negative_feedbacks
        FROM businesses b
        WHERE b.business_id = ?
    """;
        return jdbcTemplate.queryForObject(sql, Integer.class, businessId);
        } catch (Exception e) {
            logger.error("Error getting negative feedbacks: " + e.getMessage());
            return 0;
        }
    }

    private int getRemainingDaysToEndPlan(Long businessId) {
        try{
        String sql = """
        SELECT COALESCE(DATE_PART('day', b.plan_end_date - CURRENT_DATE), 0) AS remaining_days_to_end_plan
        FROM businesses b
        WHERE b.business_id = ?
    """;
        return jdbcTemplate.queryForObject(sql, Integer.class, businessId);
        } catch (Exception e) {
            logger.error("Error getting remaining days to end plan: " + e.getMessage());
            return 0;
        }
    }



    public List<OngoingAdvertisementDTO> getOngoingAdvertisements(Long businessId) {
        String sql = """
            SELECT 
                ad.image AS ad_image,
                ad.title AS ad_title,
                DATE_PART('day', ad.ad_expiration_date - CURRENT_DATE) AS remaining_days_to_end_ad
            FROM ads ad
            WHERE ad.business_id = ? AND ad.is_active = true
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            OngoingAdvertisementDTO ad = new OngoingAdvertisementDTO();
            ad.setAdImage(rs.getString("ad_image"));
            ad.setAdTitle(rs.getString("ad_title"));
            ad.setRemainingDaysToEndAd(rs.getInt("remaining_days_to_end_ad"));
            return ad;
        }, businessId);
    }

    public List<TopProductDTO> getTopProducts(Long businessId) {
        String sql = """
            SELECT 
                p.image AS product_image,
                p.name AS product_name,
                p.sales_count AS sales_count,
                COALESCE(SUM(ru.quantity * p.reservation_payment), 0.0) AS total_revenue
            FROM products p
            LEFT JOIN reserved_units ru ON p.product_id = ru.product_id
            WHERE p.business_id = ?
            GROUP BY p.product_id
            ORDER BY p.sales_count DESC
            LIMIT 5
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            TopProductDTO product = new TopProductDTO();
            product.setProductImage(rs.getString("product_image"));
            product.setProductName(rs.getString("product_name"));
            product.setSalesCount(rs.getInt("sales_count"));
            product.setTotalRevenue(rs.getDouble("total_revenue"));
            return product;
        }, businessId);
    }
}
