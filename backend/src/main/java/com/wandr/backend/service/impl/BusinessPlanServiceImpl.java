package com.wandr.backend.service.impl;

import com.wandr.backend.dao.BusinessPlanDAO;
import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.business.BusinessPlanDTO;
import com.wandr.backend.dto.business.NewBusinessPlanDTO;
import com.wandr.backend.entity.BusinessPlan;
import com.wandr.backend.service.BusinessPlanService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BusinessPlanServiceImpl implements BusinessPlanService {

    private final BusinessPlanDAO businessPlanDAO;

    public BusinessPlanServiceImpl(BusinessPlanDAO businessPlanDAO) {
        this.businessPlanDAO = businessPlanDAO;
    }

    @Override
    public ApiResponse<List<BusinessPlanDTO>> getBusinessPlans() {
        List<BusinessPlan> businessPlans = businessPlanDAO.getBusinessPlans();
        if (businessPlans.isEmpty()) {
            return new ApiResponse<>(false, 404, "No business plans found", null);
        }
        List<BusinessPlanDTO> businessPlansDTO = new ArrayList<>();
        for (BusinessPlan businessPlan : businessPlans) {
            businessPlansDTO.add(businessPlanToBusinessPlanDTO(businessPlan));
        }
        return new ApiResponse<>(true, 200, "Business Plans retrieved successfully", businessPlansDTO);
    }

    @Override
    public ApiResponse<BusinessPlanDTO> create(NewBusinessPlanDTO businessPlan) {
        BusinessPlan newBusinessPlan = new BusinessPlan();
        newBusinessPlan.setName(businessPlan.getName());
        newBusinessPlan.setDescription(businessPlan.getDescription());
        newBusinessPlan.setFeatures(businessPlan.getFeatures());
        newBusinessPlan.setPrice(businessPlan.getPrice());
        businessPlanDAO.createBusinessPlan(newBusinessPlan);

        BusinessPlanDTO businessPlanDetails = businessPlanToBusinessPlanDTO(newBusinessPlan);

        return new ApiResponse<>(true, 200, "Business Plan created successfully", businessPlanDetails);
    }

    @Override
    public ApiResponse<BusinessPlanDTO> update(Long businessPlanId, NewBusinessPlanDTO request) {
        BusinessPlan existingBusinessPlan = businessPlanDAO.findById(businessPlanId);
        if (existingBusinessPlan == null) {
            return new ApiResponse<>(false, 404, "Business Plan not found");
        }

        if (request.getName() != null) {
            existingBusinessPlan.setName(request.getName());
        }
        if (request.getDescription() != null) {
            existingBusinessPlan.setDescription(request.getDescription());
        }
        if (request.getFeatures() != null) {
            existingBusinessPlan.setFeatures(request.getFeatures());
        }
        if (request.getPrice() != null) {
            existingBusinessPlan.setPrice(request.getPrice());
        }

        businessPlanDAO.updateBusinessPlan(existingBusinessPlan);

        return new ApiResponse<>(true, 200, "Business Plan updated successfully", businessPlanToBusinessPlanDTO(existingBusinessPlan));
    }

    @Override
    public ApiResponse<Void> delete(Long businessPlanId) {
        if (businessPlanDAO.findById(businessPlanId) == null) {
            return new ApiResponse<>(false, 404, "Business Plan not found");
        }
        businessPlanDAO.deleteBusinessPlan(businessPlanId);
        return new ApiResponse<>(true, 200, "Business Plan deleted successfully");
    }

    private BusinessPlanDTO businessPlanToBusinessPlanDTO(BusinessPlan businessPlan) {
        BusinessPlanDTO businessPlanDTO = new BusinessPlanDTO();
        businessPlanDTO.setPlanId(businessPlan.getPlanId());
        businessPlanDTO.setName(businessPlan.getName());
        businessPlanDTO.setDescription(businessPlan.getDescription());
        businessPlanDTO.setFeatures(businessPlan.getFeatures());
        businessPlanDTO.setPrice(businessPlan.getPrice());
        return businessPlanDTO;
    }
}
