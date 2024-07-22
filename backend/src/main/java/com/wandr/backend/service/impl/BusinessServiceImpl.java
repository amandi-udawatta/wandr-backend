package com.wandr.backend.service.impl;

import com.wandr.backend.dao.BusinessDAO;
import com.wandr.backend.dao.BusinessPlanDAO;
import com.wandr.backend.dao.ShopCategoryDAO;
import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.UserDetailsDTO;
import com.wandr.backend.dto.UserLoginDTO;
import com.wandr.backend.dto.business.BusinessDTO;
import com.wandr.backend.dto.business.BusinessSignupDTO;
import com.wandr.backend.dto.business.PopularStoreDTO;
import com.wandr.backend.dto.business.UpdateProfileDTO;
import com.wandr.backend.entity.Business;
import com.wandr.backend.enums.Role;
import com.wandr.backend.service.BusinessService;
import com.wandr.backend.util.FileUploadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BusinessServiceImpl implements BusinessService {

    private final BusinessDAO businessDAO;
    private final ShopCategoryDAO shopCategoryDAO;
    private final BusinessPlanDAO businessPlanDAO;


    private static final Logger logger = LoggerFactory.getLogger(BusinessServiceImpl.class);

    @Autowired
    public BusinessServiceImpl(BusinessDAO businessDAO, ShopCategoryDAO shopCategoryDAO, BusinessPlanDAO businessPlanDAO) {
        this.businessDAO = businessDAO;
        this.shopCategoryDAO = shopCategoryDAO;
        this.businessPlanDAO = businessPlanDAO;
    }
    @Override
    public ApiResponse<Void> updateBusinessJwt (String jwt, Long businessId) {
        businessDAO.updateBusinessJwt(jwt, businessId);
        return new ApiResponse<>(true, 200, "JWT updated successfully");
    }

    @Override
    public String getSalt(String email){
        Optional<Business> businessOpt = businessDAO.findByEmail(email);
        if (businessOpt.isEmpty()) {
            return null;
        }
        Business business = businessOpt.get();
        return business.getSalt();
    }


    @Override
    public ApiResponse<UserDetailsDTO> loginBusiness(UserLoginDTO request) {
        Optional<Business> businessOpt = businessDAO.findByEmail(request.getEmail());
        logger.info("business opt", businessOpt);

        if (businessOpt.isEmpty()) {
            logger.error("Invalid email entered for business with email: {}", request.getEmail());
            return new ApiResponse<>(false, 401, "Invalid email", null);
        }

        if (!request.getPassword().equals(businessOpt.get().getPassword())) {
            logger.error("Invalid password for business with email: {}", request.getEmail());
            return new ApiResponse<>(false, 401, "Invalid password", null);
        }


        Business business = businessOpt.get();
        UserDetailsDTO userDetails = new UserDetailsDTO(
                business.getBusinessId(),
                business.getEmail(),
                Role.BUSINESS,
                business.getName()
        );

        logger.info("Business with email: {} logged in successfully", request.getEmail());
        return new ApiResponse<>(true, 200, "Business login successful", userDetails);
    }




    @Override
    public ApiResponse<UserDetailsDTO> registerBusiness(BusinessSignupDTO request, MultipartFile shopImageFilename, Integer shopCategory) {
        if (businessDAO.existsByEmail(request.getEmail())) {
            return new ApiResponse<>(false, 400, "Email already in use");
        }

        Business business = new Business();
        business.setName(request.getName());
        business.setDescription(request.getDescription());
        business.setServices(request.getServices());
        business.setAddress(request.getAddress());
        business.setLanguages(request.getLanguages());
        business.setWebsiteUrl(request.getWebsiteUrl());
        business.setBusinessContact(request.getBusinessContact());
        business.setBusinessType(request.getBusinessType());
        business.setShopCategory(shopCategory);
        business.setOwnerName(request.getOwnerName());
        business.setOwnerContact(request.getOwnerContact());
        business.setOwnerNic(request.getOwnerNic());
        business.setEmail(request.getEmail());
        business.setPassword(request.getPassword());
        //set default values 'pending' to status
        business.setStatus("pending");
        business.setSalt(request.getSalt());
        business.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        business.setJwt(null); // Assuming jwt_token is null for initial registration
        String shopImg = FileUploadUtil.saveFile(shopImageFilename, "business/shop_images");
        business.setShopImage(shopImg);
        businessDAO.save(business);
        //return userDetails;

        Optional<Business> businessOpt = businessDAO.findByEmail(request.getEmail());
        Business businessData = businessOpt.get();
        UserDetailsDTO userDetails = new UserDetailsDTO(
                businessData.getBusinessId(),
                businessData.getEmail(),
                Role.BUSINESS,
                businessData.getName()
        );

        return new ApiResponse<>(true, 201, "Business registered successfully", userDetails);

    }

    @Override
    public ApiResponse<String> updateProfile(Long businessId, UpdateProfileDTO request) {
        Business existingBusiness = businessDAO.findById(businessId);
        if (existingBusiness == null) {
            return new ApiResponse<>(false, 404, "Business not found");
        }

        if (request.getName() != null) {
            existingBusiness.setName(request.getName());
        }
        if (request.getEmail() != null) {
            existingBusiness.setEmail(request.getEmail());
        }
        if (request.getDescription() != null) {
            existingBusiness.setDescription(request.getDescription());
        }
        if (request.getServices() != null) {
            existingBusiness.setServices(request.getServices());
        }
        if (request.getAddress() != null) {
            existingBusiness.setAddress(request.getAddress());
        }
        if (request.getLanguages() != null) {
            existingBusiness.setLanguages(request.getLanguages());
        }
        if (request.getWebsiteUrl() != null) {
            existingBusiness.setWebsiteUrl(request.getWebsiteUrl());
        }
        if (request.getBusinessContact() != null) {
            existingBusiness.setBusinessContact(request.getBusinessContact());
        }
        if (request.getShopImage() != null) {
            existingBusiness.setShopImage(request.getShopImage());
        }
        if (request.getBusinessType() != null) {
            existingBusiness.setBusinessType(request.getBusinessType());
        }
        if (request.getShopCategory() != null) {
            existingBusiness.setShopCategory(request.getShopCategory());
        }
        if (request.getOwnerName() != null) {
            existingBusiness.setOwnerName(request.getOwnerName());
        }
        if (request.getOwnerContact() != null) {
            existingBusiness.setOwnerContact(request.getOwnerContact());
        }
        if (request.getOwnerNic() != null) {
            existingBusiness.setOwnerNic(request.getOwnerNic());
        }

        businessDAO.updateProfile(existingBusiness);

        return new ApiResponse<>(true, 200, "Profile updated successfully");
    }

//business to business dto
    private BusinessDTO businessToBusinessDto (Business business) {
        BusinessDTO businessDTO = new BusinessDTO();
        businessDTO.setName(business.getName());
        businessDTO.setEmail(business.getEmail());
        businessDTO.setDescription(business.getDescription());
        businessDTO.setServices(business.getServices());
        businessDTO.setAddress(business.getAddress());
        businessDTO.setLanguages(business.getLanguages());
        businessDTO.setWebsiteUrl(business.getWebsiteUrl());
        businessDTO.setBusinessContact(business.getBusinessContact());
        businessDTO.setShopImage(business.getShopImage());
        businessDTO.setStatus(business.getStatus());
        businessDTO.setOwnerName(business.getOwnerName());
        businessDTO.setOwnerContact(business.getOwnerContact());
        businessDTO.setOwnerNic(business.getOwnerNic());
        businessDTO.setCreatedAt(business.getCreatedAt());
        if (business.getBusinessType() == 1) {
            businessDTO.setBusinessType("Shop");
        } else if (business.getBusinessType() == 2) {
            businessDTO.setBusinessType("Service");
        }
        String shop_category = shopCategoryDAO.findNameById(business.getShopCategory());
        businessDTO.setShopCategory(shop_category);
        String business_plan = businessPlanDAO.findNameById(business.getPlanId());
        businessDTO.setPlan(business_plan);
        return businessDTO;
    }

    //get pending businesses
    @Override
    public ApiResponse<List<BusinessDTO>> getPendingBusinesses() {
        List<Business> businesses = businessDAO.getPendingBusinesses();
        List<BusinessDTO> businessDTOs = new ArrayList<>();
        for (Business business : businesses) {
            businessDTOs.add(businessToBusinessDto(business));
        }
        return new ApiResponse<>(true, 200, "Pending businesses retrieved successfully", businessDTOs);
    }

    @Override
    public ApiResponse<List<PopularStoreDTO>> getPopularStores() {
        List<PopularStoreDTO> popularStores = businessDAO.getPopularStores();
        return new ApiResponse<>(true, 200, "Popular stores retrieved successfully", popularStores);
    }


}
