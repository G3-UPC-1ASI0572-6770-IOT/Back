package com.parkingnow.space.application;

import com.parkingnow.iot.infrastructure.IotNodeRepository;
import com.parkingnow.reservation.infrastructure.ReservationRepository;
import com.parkingnow.shared.exception.ConflictException;
import com.parkingnow.shared.exception.NotFoundException;
import com.parkingnow.space.application.dto.*;
import com.parkingnow.space.domain.ParkingSpace;
import com.parkingnow.space.infrastructure.ParkingSpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ParkingSpaceService {

    private final ParkingSpaceRepository repo;
    private final ReservationRepository reservationRepo;
    private final IotNodeRepository iotNodeRepo;

    @Transactional(readOnly = true)
    public List<ParkingSpaceResponse> findByLot(Long lotId) {
        return repo.findByLotId(lotId).stream().map(ParkingSpaceResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ParkingSpaceResponse findById(Long id) {
        return repo.findById(id)
                .map(ParkingSpaceResponse::from)
                .orElseThrow(() -> new NotFoundException("Space not found: " + id));
    }

    @Transactional
    public ParkingSpaceResponse create(ParkingSpaceRequest req) {
        Long lotId = req.getParkingLotId();
        if (repo.existsByCodeAndLotId(req.getLabel(), lotId)) {
            throw new ConflictException("Space label already exists in this lot: " + req.getLabel());
        }
        ParkingSpace space = ParkingSpace.builder()
                .code(req.getLabel())
                .zone(req.getZone())
                .type(req.getType() != null ? req.getType() : "Standard")
                .sensorCode(req.getSensorCode())
                .physicalStatus(ParkingSpace.PhysicalStatus.AVAILABLE)
                .logicalStatus(ParkingSpace.LogicalStatus.AVAILABLE)
                .consolidatedStatus(ParkingSpace.ConsolidatedStatus.AVAILABLE)
                .lotId(lotId)
                .build();
        return ParkingSpaceResponse.from(repo.save(space));
    }

    @Transactional
    public ParkingSpaceResponse updateStatus(Long id, Map<String, String> body) {
        ParkingSpace space = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Space not found: " + id));
        String status = body.get("status");
        String source = body.getOrDefault("source", "MANUAL");

        ParkingSpace.ConsolidatedStatus cs;
        try {
            cs = "FREE".equalsIgnoreCase(status)
                    ? ParkingSpace.ConsolidatedStatus.AVAILABLE
                    : ParkingSpace.ConsolidatedStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            cs = ParkingSpace.ConsolidatedStatus.AVAILABLE;
        }

        space.setConsolidatedStatus(cs);
        if ("OCCUPIED".equalsIgnoreCase(status)) {
            space.setPhysicalStatus(ParkingSpace.PhysicalStatus.OCCUPIED);
        } else {
            space.setPhysicalStatus(ParkingSpace.PhysicalStatus.AVAILABLE);
        }
        space.setUpdatedAt(Instant.now());

        if ("SENSOR".equalsIgnoreCase(source)) {
            space.setIotNodeId(body.getOrDefault("nodeId", space.getIotNodeId()));
        }

        return ParkingSpaceResponse.from(repo.save(space));
    }

    public void consolidateStatus(ParkingSpace sp) {
        ParkingSpace.ConsolidatedStatus consolidated;

        if (sp.getPhysicalStatus() == ParkingSpace.PhysicalStatus.OCCUPIED) {
            consolidated = ParkingSpace.ConsolidatedStatus.OCCUPIED;
        } else if (sp.getPhysicalStatus() == ParkingSpace.PhysicalStatus.AVAILABLE
                || sp.getPhysicalStatus() == ParkingSpace.PhysicalStatus.UNKNOWN) {
            boolean hasActiveRes = reservationRepo.countActiveBySpaceId(sp.getId()) > 0;
            if (sp.getLogicalStatus() == ParkingSpace.LogicalStatus.RESERVED || hasActiveRes) {
                consolidated = ParkingSpace.ConsolidatedStatus.RESERVED;
            } else {
                consolidated = ParkingSpace.ConsolidatedStatus.AVAILABLE;
            }
        } else {
            consolidated = sp.getConsolidatedStatus() != null
                    ? sp.getConsolidatedStatus()
                    : ParkingSpace.ConsolidatedStatus.AVAILABLE;
        }

        var node = iotNodeRepo.findBySpaceId(sp.getId());
        if (node.isPresent() && node.get().getStatus() == com.parkingnow.iot.domain.IotNode.NodeStatus.OFFLINE) {
            consolidated = ParkingSpace.ConsolidatedStatus.OFFLINE;
        }

        sp.setConsolidatedStatus(consolidated);
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new NotFoundException("Space not found: " + id);
        repo.deleteById(id);
    }
}
