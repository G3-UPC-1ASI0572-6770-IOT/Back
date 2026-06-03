package com.parkingnow.parkinglot.application;

import com.parkingnow.iot.domain.IotNode;
import com.parkingnow.iot.infrastructure.IotNodeRepository;
import com.parkingnow.parkinglot.application.dto.*;
import com.parkingnow.parkinglot.domain.ParkingLot;
import com.parkingnow.parkinglot.infrastructure.ParkingLotRepository;
import com.parkingnow.shared.exception.ConflictException;
import com.parkingnow.shared.exception.NotFoundException;
import com.parkingnow.space.domain.ParkingSpace;
import com.parkingnow.space.infrastructure.ParkingSpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParkingLotService {

    private final ParkingLotRepository lotRepository;
    private final ParkingSpaceRepository spaceRepository;
    private final IotNodeRepository iotNodeRepository;

    @Transactional
    public ParkingLotResponse create(CreateParkingLotRequest req) {
        ParkingLot lot = ParkingLot.builder()
                .name(req.getName())
                .address(req.getAddress())
                .city(req.getCity())
                .capacity(req.getCapacity())
                .hourlyRate(req.getHourlyRate() != null ? req.getHourlyRate() : java.math.BigDecimal.valueOf(3.00))
                .lotType(req.getLotType() != null ? req.getLotType() : "open")
                .ownerId(req.getOwnerId())
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .build();
        lot = lotRepository.save(lot);
        return toResponse(lot);
    }

    @Transactional(readOnly = true)
    public List<ParkingLotResponse> findAll() {
        return lotRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ParkingLotResponse> findByOwner(Long ownerId) {
        return lotRepository.findByOwnerId(ownerId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Optional<ParkingLotResponse> findByOwnerId(Long ownerId) {
        return lotRepository.findFirstByOwnerId(ownerId).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ParkingLotResponse findById(Long id) {
        return lotRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Parking lot not found: " + id));
    }

    @Transactional
    public ParkingLotResponse update(Long id, CreateParkingLotRequest req) {
        ParkingLot lot = lotRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Parking lot not found: " + id));
        if (req.getName() != null) lot.setName(req.getName());
        if (req.getAddress() != null) lot.setAddress(req.getAddress());
        if (req.getCity() != null) lot.setCity(req.getCity());
        if (req.getCapacity() > 0) lot.setCapacity(req.getCapacity());
        if (req.getHourlyRate() != null) lot.setHourlyRate(req.getHourlyRate());
        if (req.getLotType() != null) lot.setLotType(req.getLotType());
        if (req.getLatitude() != null) lot.setLatitude(req.getLatitude());
        if (req.getLongitude() != null) lot.setLongitude(req.getLongitude());
        return toResponse(lotRepository.save(lot));
    }

    @Transactional
    public Map<String, String> linkNode(Long id, LinkNodeRequest req) {
        ParkingLot lot = lotRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Parking lot not found: " + id));

        lotRepository.findAll().stream()
                .filter(l -> !l.getId().equals(id) && req.getNodeId().equals(l.getNodeId()))
                .findFirst()
                .ifPresent(l -> { throw new ConflictException("Node already linked to another lot"); });

        lot.setNodeId(req.getNodeId());
        lotRepository.save(lot);

        Optional<IotNode> existingNode = iotNodeRepository.findByNodeCode(req.getNodeId());
        if (existingNode.isPresent()) {
            IotNode node = existingNode.get();
            node.setLotId(id);
            node.setUpdatedAt(Instant.now());
            iotNodeRepository.save(node);
        } else {
            IotNode node = IotNode.builder()
                    .nodeCode(req.getNodeId())
                    .lotId(id)
                    .build();
            iotNodeRepository.save(node);
        }

        return Map.of("message", "Node linked");
    }

    @Transactional
    public void delete(Long id) {
        if (!lotRepository.existsById(id)) throw new NotFoundException("Parking lot not found: " + id);
        lotRepository.deleteById(id);
    }

    private ParkingLotResponse toResponse(ParkingLot lot) {
        int occupied = spaceRepository.countByLotIdAndStatus(lot.getId(), ParkingSpace.ConsolidatedStatus.OCCUPIED);
        boolean nodeOnline = lot.getNodeId() != null &&
                iotNodeRepository.findByNodeCode(lot.getNodeId())
                        .map(n -> n.getStatus() == IotNode.NodeStatus.ONLINE)
                        .orElse(false);
        return ParkingLotResponse.from(lot, occupied, nodeOnline);
    }
}
