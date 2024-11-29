//package com.wandr.backend.scheduler;
//
//import com.wandr.backend.service.ReservationService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Component
//public class ReservationScheduler {
//
////    private final ReservationService reservationService;
////
////    @Autowired
////    public ReservationScheduler(ReservationService reservationService) {
////        this.reservationService = reservationService;
////    }
////
////    // Scheduled task to expire reservations and increase product count daily at midnight
////    @Scheduled(cron = "0 0 0 * * *") // Runs daily at midnight
////    public void processExpiredReservations() {
////        reservationService.expireReservations();
//    }
//}
