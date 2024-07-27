package com.wandr.backend.service.impl;

import com.wandr.backend.dao.TouristPlanDAO;
import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.traveller.TouristPlanDTO;
import com.wandr.backend.entity.TouristPlan;
import com.wandr.backend.service.TouristPlanService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TouristPlanServiceImpl implements TouristPlanService {

        private final TouristPlanDAO touristPlanDAO;

        public TouristPlanServiceImpl(TouristPlanDAO touristPlanDAO) {
            this.touristPlanDAO = touristPlanDAO;
        }

        @Override
        public ApiResponse<List<TouristPlanDTO>> getTouristPlans() {
            List<TouristPlan> touristPlans = touristPlanDAO.getTouristPlans();
            if (touristPlans.isEmpty()) {
                return new ApiResponse<>(false, 404, "No tourist plans found", null);
            }
            List<TouristPlanDTO> touristPlansDTO = new ArrayList<>();
            for (TouristPlan touristPlan : touristPlans) {
                touristPlansDTO.add(touristPlanToTouristPlanDTO(touristPlan));
            }
            return new ApiResponse<>(true, 200, "Tourist Plans retrieved successfully", touristPlansDTO);
        }

        @Override
        public ApiResponse<TouristPlanDTO> create(TouristPlanDTO touristPlanDTO) {
            TouristPlan newTouristPlan = new TouristPlan();
            newTouristPlan.setName(touristPlanDTO.getName());
            newTouristPlan.setDescription(touristPlanDTO.getDescription());
            newTouristPlan.setFeatures(touristPlanDTO.getFeatures());
            newTouristPlan.setPrice(touristPlanDTO.getPrice());
            touristPlanDAO.createTouristPlan(newTouristPlan);

            TouristPlanDTO touristPlanDetails = touristPlanToTouristPlanDTO(newTouristPlan);

            return new ApiResponse<>(true, 200, "Tourist Plan created successfully", touristPlanDetails);
        }

        @Override
        public ApiResponse<TouristPlanDTO> update(Long planId, TouristPlanDTO request) {
            TouristPlan existingTouristPlan = touristPlanDAO.findById(planId);
            if (existingTouristPlan == null) {
                return new ApiResponse<>(false, 404, "Tourist Plan not found");
            }
            if (request.getName() != null) {
                existingTouristPlan.setName(request.getName());
            }
            if (request.getDescription() != null) {
                existingTouristPlan.setDescription(request.getDescription());
            }
            if (request.getFeatures() != null) {
                existingTouristPlan.setFeatures(request.getFeatures());
            }
            if (request.getPrice() != null) {
                existingTouristPlan.setPrice(request.getPrice());
            }
            touristPlanDAO.updateTouristPlan(existingTouristPlan);

            return new ApiResponse<>(true, 200, "Tourist Plan updated successfully", touristPlanToTouristPlanDTO(existingTouristPlan));
        }


        @Override
        public ApiResponse<Void> delete(Long planId) {
            if (touristPlanDAO.findById(planId) == null) {
                return new ApiResponse<>(false, 404, "Tourist Plan not found");
            }
            touristPlanDAO.deleteTouristPlan(planId);
            return new ApiResponse<>(true, 200, "Tourist Plan deleted successfully");
        }

        private TouristPlanDTO touristPlanToTouristPlanDTO(TouristPlan touristPlan) {
            TouristPlanDTO touristPlanDTO = new TouristPlanDTO();
            touristPlanDTO.setName(touristPlan.getName());
            touristPlanDTO.setDescription(touristPlan.getDescription());
            touristPlanDTO.setFeatures(touristPlan.getFeatures());
            touristPlanDTO.setPrice(touristPlan.getPrice());
            return touristPlanDTO;
        }
}
