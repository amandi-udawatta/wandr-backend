package com.wandr.backend.scheduler;

import com.wandr.backend.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ReservationScheduler {

    private final ReservationService reservationService;
    private static final Logger logger = LoggerFactory.getLogger(ReservationScheduler.class);

    @Autowired
    public ReservationScheduler(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // Scheduled task to expire reservations and increase product count daily at midnight
//    @Scheduled(cron = "0 */1 * * * *") // Runs every minute
    @Scheduled(cron = "0 0 0 * * *") // Runs daily at midnight
    public void processExpiredReservations() {
        logger.info("Expiring reservations and increasing product count");
        reservationService.expireReservations();
    }
}
