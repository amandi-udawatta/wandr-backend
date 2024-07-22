package com.wandr.backend.service;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.ads.AdDTO;

import java.util.List;

public interface AdService {

    ApiResponse<List<AdDTO>> getPendingAds();


}
