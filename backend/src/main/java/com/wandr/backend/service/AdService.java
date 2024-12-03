package com.wandr.backend.service;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.ads.AdDTO;
import com.wandr.backend.dto.ads.ApprovedAdDTO;

import java.util.List;

public interface AdService {

    ApiResponse<String> createAd(AdDTO request);
    ApiResponse<List<AdDTO>> getAdsByBusinessId(Long businessId);

    ApiResponse<List<AdDTO>> getPendingAds();
    ApiResponse<List<ApprovedAdDTO>> getApprovedAds();
    ApiResponse<Void> approveAd(Long adId);
    ApiResponse<Void> declineAd(Long adId);
    ApiResponse<Void> deleteAd(Long adId);

    void deactivateExpiredAds();

    ApiResponse<Void> incrementClickCount(Long adId);




    }
