package com.parkingnow.dashboard.application;

import com.parkingnow.iot.domain.IotNode;
import com.parkingnow.iot.infrastructure.IotEventRepository;
import com.parkingnow.iot.infrastructure.IotNodeRepository;
import com.parkingnow.parkinglot.infrastructure.ParkingLotRepository;
import com.parkingnow.payment.infrastructure.PaymentRepository;
import com.parkingnow.reservation.domain.Reservation;
import com.parkingnow.reservation.infrastructure.ReservationRepository;
import com.parkingnow.space.domain.ParkingSpace;
import com.parkingnow.space.infrastructure.ParkingSpaceRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ParkingLotRepository lotRepo;
    private final ParkingSpaceRepository spaceRepo;
    private final ReservationRepository reservationRepo;
    private final PaymentRepository paymentRepo;
    private final IotNodeRepository iotNodeRepo;
    private final IotEventRepository iotEventRepo;

    @Data @Builder
    public static class DashboardSummary {
        private int totalSpaces;
        private int freeSpaces;
        private int occupiedSpaces;
        private int reservedSpaces;
        private double occupancyRate;
        private BigDecimal todayRevenue;
        private int activeReservations;
        private String nodeStatus;
        private Instant nodeLastSeen;
        private List<Map<String, Object>> last7DaysRevenue;
        private List<Map<String, Object>> recentIotEvents;
    }

    @Transactional(readOnly = true)
    public DashboardSummary getSummary(Long ownerId) {
        List<Long> lotIds;
        if (ownerId != null) {
            lotIds = lotRepo.findByOwnerId(ownerId).stream()
                    .map(l -> l.getId()).toList();
        } else {
            lotIds = lotRepo.findAll().stream().map(l -> l.getId()).toList();
        }

        int total = lotIds.stream()
                .mapToInt(id -> spaceRepo.findByLotId(id).size()).sum();
        int occupied = countSpaces(lotIds, ParkingSpace.ConsolidatedStatus.OCCUPIED);
        int reserved = countSpaces(lotIds, ParkingSpace.ConsolidatedStatus.RESERVED);
        int free = countSpaces(lotIds, ParkingSpace.ConsolidatedStatus.AVAILABLE);
        double occupancyRate = total > 0 ? ((double) occupied / total) * 100.0 : 0.0;

        int activeReservations = lotIds.stream()
                .mapToInt(id -> reservationRepo.countByLotIdAndStatus(id, Reservation.ReservationStatus.ACTIVE))
                .sum();

        Instant startOfDay = Instant.now().truncatedTo(ChronoUnit.DAYS);
        BigDecimal todayRevenue = paymentRepo.sumPaidSince(startOfDay);
        if (todayRevenue == null) todayRevenue = BigDecimal.ZERO;

        List<IotNode> nodes = lotIds.stream()
                .flatMap(id -> iotNodeRepo.findByLotId(id).stream()).toList();
        String nodeStatus = nodes.isEmpty() ? "OFFLINE"
                : nodes.stream().anyMatch(n -> n.getStatus() == IotNode.NodeStatus.ONLINE) ? "ONLINE" : "OFFLINE";
        Instant nodeLastSeen = nodes.stream()
                .map(IotNode::getLastHeartbeatAt)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);

        List<Map<String, Object>> last7Days = buildLast7Days();
        List<Map<String, Object>> recentEvents = buildRecentEvents();

        return DashboardSummary.builder()
                .totalSpaces(total)
                .freeSpaces(free)
                .occupiedSpaces(occupied)
                .reservedSpaces(reserved)
                .occupancyRate(Math.round(occupancyRate * 100.0) / 100.0)
                .todayRevenue(todayRevenue)
                .activeReservations(activeReservations)
                .nodeStatus(nodeStatus)
                .nodeLastSeen(nodeLastSeen)
                .last7DaysRevenue(last7Days)
                .recentIotEvents(recentEvents)
                .build();
    }

    private int countSpaces(List<Long> lotIds, ParkingSpace.ConsolidatedStatus status) {
        return lotIds.stream()
                .mapToInt(id -> spaceRepo.countByLotIdAndStatus(id, status))
                .sum();
    }

    private List<Map<String, Object>> buildLast7Days() {
        Instant since = Instant.now().minus(7, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
        List<Object[]> rows = paymentRepo.sumByDay(since);

        Map<String, BigDecimal> byDate = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            String d = LocalDate.now(ZoneOffset.UTC).minusDays(i).toString();
            byDate.put(d, BigDecimal.ZERO);
        }
        for (Object[] row : rows) {
            if (row[0] != null) {
                byDate.put(row[0].toString(), row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO);
            }
        }

        return byDate.entrySet().stream()
                .map(e -> Map.<String, Object>of("date", e.getKey(), "amount", e.getValue()))
                .toList();
    }

    private List<Map<String, Object>> buildRecentEvents() {
        return iotEventRepo.findAllByOrderByReceivedAtDesc(
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "receivedAt")))
                .stream()
                .map(e -> Map.<String, Object>of(
                        "ts", e.getReceivedAt().toString(),
                        "spaceLabel", e.getSpaceCode() != null ? e.getSpaceCode() : "",
                        "status", e.getDetectedStatus().name()
                ))
                .toList();
    }
}
