package com.wandr.backend.service.impl;

import com.wandr.backend.dao.AdDAO;
import com.wandr.backend.dao.BusinessDAO;
import com.wandr.backend.dao.BusinessPlanDAO;
import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.ads.AdDTO;
import com.wandr.backend.entity.Ad;
import com.wandr.backend.entity.Business;
import com.wandr.backend.service.AdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdServiceImpl implements AdService {

    private final AdDAO adDAO;
    private final BusinessDAO businessDAO;

    private final BusinessPlanDAO businessPlanDAO;

    @Value("${core.backend.url}")
    private String backendUrl;

    private static final Logger logger = LoggerFactory.getLogger(AdServiceImpl.class);

    @Autowired
    public AdServiceImpl(AdDAO adDAO, BusinessDAO businessDAO, BusinessPlanDAO businessPlanDAO) {
        this.adDAO = adDAO;
        this.businessDAO = businessDAO;
        this.businessPlanDAO = businessPlanDAO;
    }

    @Override
    public ApiResponse<List<AdDTO>> getPendingAds() {
        //if no pending ads, return null
        if (adDAO.getPendingAds().isEmpty()) {
            return new ApiResponse<>(false, 404, "No pending advertisements found", null);
        }
        List<Ad> pendingAds =  adDAO.getPendingAds();
        List<AdDTO> pendingAdsDTO = new ArrayList<>();
        for (Ad ad : pendingAds) {
            pendingAdsDTO.add(adToAdDTO(ad));
        }
        return new ApiResponse<>(true, 200, "Pending Advertisements retrieved successfully", pendingAdsDTO);
    }

    //ad to ad dto
    private AdDTO adToAdDTO(Ad ad) {
        AdDTO adDto = new AdDTO();
        adDto.setShopName(businessDAO.getBusinessNameById(ad.getBusinessId()));
        adDto.setBusinessId(ad.getBusinessId());
        adDto.setTitle(ad.getTitle());
        adDto.setDescription(ad.getDescription());
        String imageUri = backendUrl + "/ads/" + ad.getImage();
        adDto.setImage(imageUri);
        adDto.setImage(ad.getImage());
        Business business = businessDAO.findById(ad.getBusinessId());
        adDto.setBusinessPlan(businessPlanDAO.findNameById(business.getPlanId()));
        adDto.setRequestedDate(ad.getRequestedDate());
        adDto.setStatus(ad.getStatus());
        return adDto;
    }


}
