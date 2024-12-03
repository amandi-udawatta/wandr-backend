package com.wandr.backend.scheduler;

import com.wandr.backend.service.AdService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AdScheduler {

    private final AdService adService;

    public AdScheduler(AdService adService) {
        this.adService = adService;
    }

    @Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
    public void deactivateExpiredAdsTask() {
        adService.deactivateExpiredAds();
    }
}
