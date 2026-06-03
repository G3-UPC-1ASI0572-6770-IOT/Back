package com.parkingnow.shared.config;

import com.parkingnow.iot.application.IotNodeService;
import com.parkingnow.reservation.application.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MaintenanceScheduler {

    private final ReservationService reservationService;
    private final IotNodeService iotNodeService;

    @Scheduled(fixedDelayString = "${app.maintenance.interval-ms:30000}")
    public void refreshOperationalState() {
        reservationService.expireStale();
        iotNodeService.checkOfflineNodes(90);
    }
}
