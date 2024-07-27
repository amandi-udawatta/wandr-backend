package com.wandr.backend.service;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.business.*;
import com.wandr.backend.entity.BusinessPlan;

import java.util.List;

public interface BusinessPlanService {

    ApiResponse<List<BusinessPlanDTO>> getBusinessPlans();

    ApiResponse<BusinessPlanDTO> create(NewBusinessPlanDTO businessPlan);

    ApiResponse<BusinessPlanDTO> update(Long businessPlanId, NewBusinessPlanDTO businessPlan);

    ApiResponse<Void> delete(Long businessPlanId);



    }
