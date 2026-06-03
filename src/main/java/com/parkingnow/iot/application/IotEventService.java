package com.parkingnow.iot.application;

import com.parkingnow.iot.application.dto.IotEventRequest;
import com.parkingnow.iot.application.dto.IotEventResponse;
import com.parkingnow.iot.domain.IotEvent;
import com.parkingnow.iot.domain.IotNode;
import com.parkingnow.iot.infrastructure.IotEventRepository;
import com.parkingnow.iot.infrastructure.IotNodeRepository;
import com.parkingnow.reservation.domain.Reservation;
import com.parkingnow.reservation.infrastructure.ReservationRepository;
import com.parkingnow.space.application.ParkingSpaceService;
import com.parkingnow.space.domain.ParkingSpace;
import com.parkingnow.space.infrastructure.ParkingSpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IotEventService {

    private final IotEventRepository eventRepo;
    private final ParkingSpaceRepository spaceRepo;
    private final IotNodeRepository nodeRepo;
    private final ReservationRepository reservationRepo;
    private final ParkingSpaceService spaceService;

    @Transactional
    public IotEventResponse receiveEvent(IotEventRequest req) {
        String statusStr = req.resolvedStatus();
        IotEvent.DetectedStatus detected = "OCCUPIED".equalsIgnoreCase(statusStr)
                ? IotEvent.DetectedStatus.OCCUPIED
                : IotEvent.DetectedStatus.AVAILABLE;

        IotEvent event = IotEvent.builder()
                .nodeId(req.getNodeId())
                .spaceCode(req.resolvedSpaceLabel())
                .distanceCm(req.getDistanceCm())
                .detectedStatus(detected)
                .receivedAt(Instant.now())
                .parkingSpaceId(req.getParkingSpaceId())
                .build();
        event = eventRepo.save(event);

        syncSpaceState(event);
        return IotEventResponse.from(event);
    }

    private void syncSpaceState(IotEvent event) {
        var nodeOpt = nodeRepo.findByNodeCode(event.getNodeId());
        if (nodeOpt.isEmpty()) return;

        Long spaceId = nodeOpt.get().getSpaceId();
        if (spaceId == null) {
            spaceRepo.findByLotIdAndCode(nodeOpt.get().getLotId(), event.getSpaceCode())
                    .ifPresent(space -> updateSpace(space, event));
            return;
        }
        spaceRepo.findById(spaceId).ifPresent(space -> updateSpace(space, event));
    }

    private void updateSpace(ParkingSpace sp, IotEvent event) {
        ParkingSpace.PhysicalStatus physical = event.getDetectedStatus() == IotEvent.DetectedStatus.OCCUPIED
                ? ParkingSpace.PhysicalStatus.OCCUPIED
                : ParkingSpace.PhysicalStatus.AVAILABLE;
        sp.setPhysicalStatus(physical);
        spaceService.consolidateStatus(sp);
        spaceRepo.save(sp);

        event.setSyncedAt(Instant.now());
        event.setParkingSpaceId(sp.getId());
        eventRepo.save(event);

        if (physical == ParkingSpace.PhysicalStatus.OCCUPIED) {
            consumeActiveReservationForSpace(sp.getId());
        }
    }

    private void consumeActiveReservationForSpace(Long spaceId) {
        reservationRepo.findBySpaceId(spaceId).stream()
                .filter(r -> r.getStatus() == Reservation.ReservationStatus.ACTIVE)
                .forEach(r -> {
                    r.setStatus(Reservation.ReservationStatus.CONSUMED);
                    r.setConsumedAt(Instant.now());
                    reservationRepo.save(r);
                });
    }

    @Transactional(readOnly = true)
    public List<IotEventResponse> findAll(int limit) {
        return eventRepo.findAllByOrderByReceivedAtDesc(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "receivedAt")))
                .stream().map(IotEventResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<IotEventResponse> findByNode(String nodeId, int limit) {
        return eventRepo.findByNodeIdOrderByReceivedAtDesc(nodeId,
                PageRequest.of(0, limit)).stream()
                .map(IotEventResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public long countEventsSince(Instant since) {
        return eventRepo.countByReceivedAtAfter(since);
    }
}
