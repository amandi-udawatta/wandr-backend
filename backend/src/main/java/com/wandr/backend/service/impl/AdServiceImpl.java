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
    public ApiResponse<Void> createAd(AdDTO request) {
        try {
            // Fetch the business details
            Business business = businessDAO.findById(request.getBusinessId());
            if (business == null) {
                return new ApiResponse<>(false, 404, "Business not found", null);
            }

            // Ensure the business has a valid plan
            if (business.getPlanId() == null) {
                return new ApiResponse<>(false, 400, "No plan associated with this business. Please purchase a plan to proceed.", null);
            }

            int planId = business.getPlanId(); // Fetch the plan ID
            long currentAdCount = adDAO.countAdsByBusinessId(request.getBusinessId()); // Count existing ads
            logger.info("Current ad count: {}", currentAdCount);
            logger.info("Plan ID: {}", planId);

            // Plan-specific validations
            if (planId == 1 && currentAdCount >= 1) {
                logger.info("plan 1 exceeded: {}", currentAdCount);
                // Plan ID 1: Basic (1 Ad Slot)
                return new ApiResponse<>(false, 400, "Your plan allows only 1 ad. Upgrade your plan to create more ads.", null);
            } else if (planId == 2 && currentAdCount >= 3) {
                // Plan ID 2: Standard (3 Ad Slots)
                return new ApiResponse<>(false, 400, "Your plan allows up to 3 ads. Upgrade your plan to create more ads.", null);
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

            adDAO.saveAd(ad); // Save the ad to the database

            return new ApiResponse<>(true, 201, "Ad created successfully", null);

        } catch (Exception e) {
            logger.error("Error creating ad for business ID: {}", request.getBusinessId(), e);
            return new ApiResponse<>(false, 500, "An error occurred while creating the ad.", null);
        }
    }


    @Override
    public ApiResponse<List<AdDTO>> getPendingAds() {
        //if no pending ads, return null
        if (adDAO.getPendingAds().isEmpty()) {
            return new ApiResponse<>(false, 404, "No pending advertisements found", null);
        }
        List<Ad> pendingAds = adDAO.getPendingAds();
        List<AdDTO> pendingAdsDTO = new ArrayList<>();
        for (Ad ad : pendingAds) {
            pendingAdsDTO.add(adToAdDTO(ad));
        }
        return new ApiResponse<>(true, 200, "Pending Advertisements retrieved successfully", pendingAdsDTO);
    }

    @Override
    public ApiResponse<List<ApprovedAdDTO>> getApprovedAds() {
        //if no pending ads, return null
        if (adDAO.getApprovedAds().isEmpty()) {
            return new ApiResponse<>(false, 404, "No approved advertisements found", null);
        }
        List<Ad> approvedAds = adDAO.getApprovedAds();
        List<ApprovedAdDTO> approvedAdDTO = new ArrayList<>();
        for (Ad ad : approvedAds) {
            approvedAdDTO.add(adToApprovedAdDTO(ad));
        }
        return new ApiResponse<>(true, 200, "Approved Advertisements retrieved successfully", approvedAdDTO);
    }

    @Override
    public ApiResponse<Void> approveAd(Long adId) {
        Ad ad = adDAO.findById(adId);
        if (ad == null) {
            return new ApiResponse<>(false, 404, "Ad not found", null);
        }
        adDAO.setStatus(adId, "approved");
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
        String imageUri = "/business/ads/" + ad.getImage();
        adDto.setImage(imageUri);
        adDto.setImage(ad.getImage());
        Business business = businessDAO.findById(ad.getBusinessId());
        adDto.setBusinessPlan(businessPlanDAO.findNameById(business.getPlanId()));
        adDto.setRequestedDate(ad.getRequestedDate());
        adDto.setStatus(ad.getStatus());
        return adDto;
    }

    private ApprovedAdDTO adToApprovedAdDTO(Ad ad) {
        ApprovedAdDTO adDto = new ApprovedAdDTO();
        adDto.setAdId(ad.getAdId());
        adDto.setShopName(businessDAO.getBusinessNameById(ad.getBusinessId()));
        adDto.setBusinessId(ad.getBusinessId());
        adDto.setTitle(ad.getTitle());
        adDto.setDescription(ad.getDescription());
        String imageUri = "/business/ads/" + ad.getImage();
        adDto.setImage(imageUri);
        adDto.setImage(ad.getImage());
        Business business = businessDAO.findById(ad.getBusinessId());
        adDto.setBusinessPlan(businessPlanDAO.findNameById(business.getPlanId()));
        adDto.setPostedDate(business.getPaidDate());
        Timestamp planEndTimestamp = business.getPlanEndDate();
        LocalDateTime planEndDate = planEndTimestamp.toLocalDateTime();
        LocalDateTime currentDateTime = LocalDateTime.now();
        Integer remainingDays = (int) ChronoUnit.DAYS.between(currentDateTime, planEndDate);
        adDto.setRemainingDays(remainingDays);
        adDto.setStatus(ad.getStatus());
        return adDto;
    }

//    public void approvePendingAds() {
//        int rowsUpdated = adDAO.approvePendingAds();
//        if (rowsUpdated > 0) {
//            System.out.println(rowsUpdated + " ad(s) updated to 'approved' status.");
//        } else {
//            System.out.println("No pending ads to approve.");
//        }
//    }

    public ApiResponse<Void> deleteAd(Long adId) {
        Ad ad = adDAO.findById(adId);
        if (ad == null) {
            return new ApiResponse<>(false, 404, "Ad not found", null);
        }
        adDAO.deleteAd(adId);
        return new ApiResponse<>(true, 200, "Ad deleted successfully", null);
    }
}
