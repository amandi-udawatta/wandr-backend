package com.wandr.backend.service;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.business.*;
import com.wandr.backend.entity.BusinessPlan;

import java.util.List;

public interface BusinessPlanService {

    ApiResponse<List<BusinessPlanDTO>> getBusinessPlans();

    ApiResponse<BusinessPlanDTO> create(BusinessPlanDTO businessPlan);

    ApiResponse<BusinessPlanDTO> update(Long businessPlanId, BusinessPlanDTO businessPlan);

    ApiResponse<Void> delete(Long businessPlanId);



    }
