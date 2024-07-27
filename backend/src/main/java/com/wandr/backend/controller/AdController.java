package com.wandr.backend.controller;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.ads.AdDTO;
import com.wandr.backend.dto.ads.ApprovedAdDTO;
import com.wandr.backend.service.AdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ads")
public class AdController {

    private final AdService adService;
    private static final Logger logger = LoggerFactory.getLogger(AdController.class);

    @Autowired
    public AdController(AdService adService) {
        this.adService = adService;
    }



    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<AdDTO>>> getPendingAds() {
        try{
            return ResponseEntity.ok(adService.getPendingAds());
        } catch (Exception e) {
            logger.error("Error getting pending advertisements", e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "Error getting pending advertisements", null));
        }
    }

    @GetMapping("/approved")
    public  ResponseEntity<ApiResponse<List<ApprovedAdDTO>>> getApprovedAdds() {
        try{
            return ResponseEntity.ok(adService.getApprovedAds());
        }
        catch (Exception e) {
            logger.error("Error getting approved advertisements", e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "Error getting approved advertisements", null));
        }
    }

    //approve an ad
    @GetMapping("/approve/{adId}")
    public ResponseEntity<ApiResponse<Void>> approveAd(@PathVariable Long adId) {
        try{
            return ResponseEntity.ok(adService.approveAd(adId));
        } catch (Exception e) {
            logger.error("Error approving advertisement with id: {}", adId, e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "Error approving advertisement", null));
        }
    }

    //decline ad
    @GetMapping("/decline/{adId}")
    public ResponseEntity<ApiResponse<Void>> declineAd(@PathVariable Long adId) {
        try{
            return ResponseEntity.ok(adService.declineAd(adId));
        } catch (Exception e) {
            logger.error("Error declining advertisement with id: {}", adId, e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "Error declining advertisement", null));
        }
    }

}
