package com.parkingnow.dashboard.application;

import com.parkingnow.event.application.EventAlertService;
import com.parkingnow.event.application.dto.EventAlertResponse;
import com.parkingnow.parkinglot.application.ParkingLotService;
import com.parkingnow.parkinglot.application.dto.ParkingLotResponse;
import com.parkingnow.payment.infrastructure.PaymentRepository;
import com.parkingnow.reservation.infrastructure.ReservationRepository;
import com.parkingnow.reservation.domain.Reservation;
import com.parkingnow.space.infrastructure.ParkingSpaceRepository;
import com.parkingnow.event.infrastructure.EventAlertRepository;
import com.parkingnow.event.domain.EventAlert;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ParkingLotService lotService;
    private final ParkingSpaceRepository spaceRepo;
    private final ReservationRepository reservationRepo;
    private final EventAlertRepository eventRepo;
    private final EventAlertService eventService;
    private final PaymentRepository paymentRepo;

    @Data @Builder
    public static class DashboardSummary {
        private int totalSpaces;
        private int occupiedSpaces;
        private int occupancyPercent;
        private int activeReservations;
        private int alertsToday;
        private int totalLots;
        private int totalNodes;
        private int paidReservationsToday;
        private BigDecimal estimatedRevenueToday;
        private List<ParkingLotResponse> topLots;
        private List<EventAlertResponse> recentEvents;
    }

    public DashboardSummary getSummary(Long ownerId) {
        List<ParkingLotResponse> lots = ownerId != null
                ? lotService.findByOwner(ownerId)
                : lotService.findAll();

        int totalSpaces = lots.stream().mapToInt(ParkingLotResponse::getCapacity).sum();
        int occupied = lots.stream().mapToInt(ParkingLotResponse::getOccupied).sum();
        int occupancy = totalSpaces > 0 ? (occupied * 100 / totalSpaces) : 0;
        int activeReservations = (int) reservationRepo.countByLotIdAndStatus(
                lots.isEmpty() ? -1L : lots.get(0).getId(),
                Reservation.ReservationStatus.ACTIVE);
        int alerts = eventRepo.countBySeverity(EventAlert.Severity.CRITICAL)
                + eventRepo.countBySeverity(EventAlert.Severity.WARNING);

        Instant startOfDay = Instant.now().truncatedTo(ChronoUnit.DAYS);
        int paidToday = paymentRepo.countPaidSince(startOfDay);
        BigDecimal revenueToday = paymentRepo.sumPaidSince(startOfDay);

        return DashboardSummary.builder()
                .totalSpaces(totalSpaces)
                .occupiedSpaces(occupied)
                .occupancyPercent(occupancy)
                .activeReservations(activeReservations)
                .alertsToday(alerts)
                .totalLots(lots.size())
                .totalNodes(lots.stream().mapToInt(ParkingLotResponse::getIotNodes).sum())
                .paidReservationsToday(paidToday)
                .estimatedRevenueToday(revenueToday)
                .topLots(lots)
                .recentEvents(eventService.findAll(5))
                .build();
    }
}
