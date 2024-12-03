package com.wandr.backend.service.impl;

import com.wandr.backend.dao.AdDAO;
import com.wandr.backend.dao.BusinessDAO;
import com.wandr.backend.dao.BusinessPlanDAO;
import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.ads.AdDTO;
import com.wandr.backend.dto.ads.ApprovedAdDTO;
import com.wandr.backend.dto.business.PaidBusinessDTO;
import com.wandr.backend.entity.Ad;
import com.wandr.backend.entity.Business;
import com.wandr.backend.service.AdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdServiceImpl implements AdService {

    private final AdDAO adDAO;
    private final BusinessDAO businessDAO;

    private final BusinessPlanDAO businessPlanDAO;

    private static final Logger logger = LoggerFactory.getLogger(AdServiceImpl.class);

    @Autowired
    public AdServiceImpl(AdDAO adDAO, BusinessDAO businessDAO, BusinessPlanDAO businessPlanDAO) {
        this.adDAO = adDAO;
        this.businessDAO = businessDAO;
        this.businessPlanDAO = businessPlanDAO;
    }

    @Override
    public ApiResponse<String> createAd(AdDTO request) {
        try {
            // Fetch the business details
            Business business = businessDAO.findById(request.getBusinessId());
            if (business == null) {
                return new ApiResponse<>(false, 404, "Business not found");
            }

            // Ensure the business has a valid plan
            if (business.getPlanId() == null) {
                return new ApiResponse<>(false, 400, "No plan associated with this business. Please purchase a plan to proceed.", "LIMIT_EXCEEDED");
            }

            int planId = business.getPlanId(); // Fetch the plan ID
            long currentAdCount = adDAO.countAdsByBusinessId(request.getBusinessId()); // Count existing ads
            logger.info("Current ad count: {}", currentAdCount);
            logger.info("Plan ID: {}", planId);

            // Plan-specific validations
            if (planId == 1 && currentAdCount >= 1) {
                logger.info("plan 1 exceeded: {}", currentAdCount);
                // Plan ID 1: Basic (1 Ad Slot)
                return new ApiResponse<>(false, 400, "Your plan allows only 1 ad. Upgrade your plan to create more ads.", "LIMIT_EXCEEDED");
            } else if (planId == 2 && currentAdCount >= 3) {
                // Plan ID 2: Standard (3 Ad Slots)
                return new ApiResponse<>(false, 400, "Your plan allows up to 3 ads. Upgrade your plan to create more ads.", "LIMIT_EXCEEDED");
            }
            // Plan ID 3 (Premium): No restriction, no additional checks needed

            // Create the ad
            Ad ad = new Ad();
            ad.setBusinessId(request.getBusinessId());
            ad.setTitle(request.getTitle());
            ad.setDescription(request.getDescription());
            ad.setImage(request.getImage());
            ad.setRequestedDate(Timestamp.valueOf(LocalDateTime.now()));
            ad.setStatus("pending"); // Default status for new ads
            ad.setAdStartDate(null); // Start date will be set on approval
            ad.setAdExpirationDate(null); // Expiration will be set on approval
            ad.setActive(false); // Default active status
            ad.setViewCount(0); // Initialize view count
            ad.setClickCount(0); // Initialize click count

            adDAO.saveAd(ad); // Save the ad to the database

            return new ApiResponse<>(true, 201, "Ad created successfully", null);

        } catch (Exception e) {
            logger.error("Error creating ad for business ID: {}", request.getBusinessId(), e);
            return new ApiResponse<>(false, 500, "An error occurred while creating the ad.", null);
        }
    }

    @Override
    public ApiResponse<List<AdDTO>> getAdsByBusinessId(Long businessId) {
        List<Ad> ads = adDAO.getAdsByBusinessId(businessId);
        if (ads.isEmpty()) {
            return new ApiResponse<>(false, 404, "No advertisements found for the business", null);
        }

        List<AdDTO> adDTOs = ads.stream()
                .map(this::adToAdDTO)
                .collect(Collectors.toList());

        return new ApiResponse<>(true, 200, "Advertisements retrieved successfully", adDTOs);
    }


    @Override
    public ApiResponse<List<AdDTO>> getPendingAds() {
        List<Ad> pendingAds = adDAO.getPendingAds();
        if (pendingAds.isEmpty()) {
            return new ApiResponse<>(false, 404, "No pending advertisements found", null);
        }

        List<AdDTO> adDTOs = pendingAds.stream()
                .map(this::adToAdDTO)
                .collect(Collectors.toList());

        return new ApiResponse<>(true, 200, "Pending advertisements retrieved successfully", adDTOs);
    }

    @Override
    public ApiResponse<List<ApprovedAdDTO>> getApprovedAds() {
        List<Ad> approvedAds = adDAO.getApprovedAds();
        if (approvedAds.isEmpty()) {
            return new ApiResponse<>(false, 404, "No approved advertisements found", null);
        }

        List<ApprovedAdDTO> approvedAdDTOs = approvedAds.stream()
                .map(this::adToApprovedAdDTO)
                .collect(Collectors.toList());

        return new ApiResponse<>(true, 200, "Approved advertisements retrieved successfully", approvedAdDTOs);
    }


    @Override
    public ApiResponse<Void> approveAd(Long adId) {
        Ad ad = adDAO.findById(adId);
        if (ad == null) {
            return new ApiResponse<>(false, 404, "Ad not found", null);
        }

        // Set start and expiration dates
        Timestamp startDate = new Timestamp(System.currentTimeMillis());
        Timestamp expirationDate = Timestamp.valueOf(startDate.toLocalDateTime().plusMonths(1));
        ad.setAdStartDate(startDate);
        ad.setAdExpirationDate(expirationDate);
        ad.setActive(true);

        adDAO.updateAd(ad);

        return new ApiResponse<>(true, 200, "Ad approved successfully", null);
    }


    @Override
    public ApiResponse<Void> declineAd(Long adId) {
        Ad ad = adDAO.findById(adId);
        if (ad == null) {
            return new ApiResponse<>(false, 404, "Ad not found", null);
        }
        adDAO.setStatus(adId, "declined");
        return new ApiResponse<>(true, 200, "Ad declined successfully", null);
    }

    //ad to ad dto
    private AdDTO adToAdDTO(Ad ad) {
        AdDTO adDto = new AdDTO();
        adDto.setAdId(ad.getAdId());
        adDto.setShopName(businessDAO.getBusinessNameById(ad.getBusinessId()));
        adDto.setBusinessId(ad.getBusinessId());
        adDto.setTitle(ad.getTitle());
        adDto.setDescription(ad.getDescription());
        adDto.setImage(ad.getImage());
        adDto.setBusinessPlan(businessPlanDAO.findNameById(ad.getBusinessId()));
        adDto.setRequestedDate(ad.getRequestedDate());
        adDto.setAdStartDate(ad.getAdStartDate()); // New field
        adDto.setAdExpirationDate(ad.getAdExpirationDate()); // New field
        adDto.setStatus(ad.getStatus());
        adDto.setActive(ad.isActive());
        adDto.setClickCount(ad.getClickCount());
        if (ad.getAdExpirationDate() != null) {
            LocalDateTime expiration = ad.getAdExpirationDate().toLocalDateTime();
            LocalDateTime now = LocalDateTime.now();
            int remainingDays = (int) ChronoUnit.DAYS.between(now, expiration);
            adDto.setRemainingDays(Math.max(0, remainingDays)); // Avoid negative days
        } else {
            adDto.setRemainingDays(null); // No expiration date means unlimited or undefined
        }

        return adDto;
    }

    private ApprovedAdDTO adToApprovedAdDTO(Ad ad) {
        ApprovedAdDTO adDto = new ApprovedAdDTO();
        adDto.setAdId(ad.getAdId());
        adDto.setShopName(businessDAO.getBusinessNameById(ad.getBusinessId()));
        adDto.setBusinessId(ad.getBusinessId());
        adDto.setTitle(ad.getTitle());
        adDto.setDescription(ad.getDescription());
        adDto.setImage(ad.getImage());
        adDto.setBusinessPlan(businessPlanDAO.findNameById(ad.getBusinessId()));
        adDto.setRequestedDate(ad.getRequestedDate());
        adDto.setAdStartDate(ad.getAdStartDate()); // New field
        adDto.setAdExpirationDate(ad.getAdExpirationDate()); // New field
        adDto.setStatus(ad.getStatus());
        adDto.setActive(ad.isActive());
        adDto.setClickCount(ad.getClickCount());
        if (ad.getAdExpirationDate() != null) {
            LocalDateTime expirationDate = ad.getAdExpirationDate().toLocalDateTime();
            LocalDateTime now = LocalDateTime.now();
            adDto.setRemainingDays((int) ChronoUnit.DAYS.between(now, expirationDate));
        }
        return adDto;
    }

    public ApiResponse<Void> deleteAd(Long adId) {
        Ad ad = adDAO.findById(adId);
        if (ad == null) {
            return new ApiResponse<>(false, 404, "Ad not found", null);
        }
        adDAO.deleteAd(adId);
        return new ApiResponse<>(true, 200, "Ad deleted successfully", null);
    }

    @Override
    public void deactivateExpiredAds() {
        List<Ad> expiredAds = adDAO.findExpiredAds();
        for (Ad ad : expiredAds) {
            ad.setActive(false);
            adDAO.updateAd(ad);
        }
    }

    @Override
    public ApiResponse<Void> incrementClickCount(Long adId) {
        Ad ad = adDAO.findById(adId);
        if (ad == null) {
            return new ApiResponse<>(false, 404, "Ad not found", null);
        }

        ad.setClickCount(ad.getClickCount() + 1);
        adDAO.updateAd(ad);

        return new ApiResponse<>(true, 200, "Click count incremented", null);
    }


}
