package com.wandr.backend.service;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.traveller.TouristPlanDTO;

import java.util.List;

public interface TouristPlanService {

    ApiResponse<List<TouristPlanDTO>> getTouristPlans();

    ApiResponse<TouristPlanDTO> create(TouristPlanDTO touristPlanDTO);

    ApiResponse<TouristPlanDTO> update(Long planId, TouristPlanDTO touristPlanDTO);

    ApiResponse<Void> delete(Long planId);



    }
