package com.parkingnow.space.application;

import com.parkingnow.shared.exception.ConflictException;
import com.parkingnow.shared.exception.NotFoundException;
import com.parkingnow.space.application.dto.*;
import com.parkingnow.space.domain.ParkingSpace;
import com.parkingnow.space.infrastructure.ParkingSpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingSpaceService {

    private final ParkingSpaceRepository repo;

    public List<ParkingSpaceResponse> findByLot(Long lotId) {
        return repo.findByLotId(lotId).stream().map(ParkingSpaceResponse::from).toList();
    }

    public ParkingSpaceResponse findById(Long id) {
        return repo.findById(id).map(ParkingSpaceResponse::from)
                .orElseThrow(() -> new NotFoundException("Space not found: " + id));
    }

    @Transactional
    public ParkingSpaceResponse create(Long lotId, ParkingSpaceRequest req) {
        if (repo.existsByCodeAndLotId(req.getCode(), lotId)) {
            throw new ConflictException("Space code already exists in this lot: " + req.getCode());
        }
        ParkingSpace space = ParkingSpace.builder()
                .code(req.getCode())
                .zone(req.getZone())
                .type(req.getType() != null ? req.getType() : "Standard")
                .sensorCode(req.getSensorCode())
                .status(parseStatus(req.getStatus()))
                .lotId(lotId)
                .build();
        return ParkingSpaceResponse.from(repo.save(space));
    }

    @Transactional
    public ParkingSpaceResponse updateStatus(Long id, String status) {
        ParkingSpace space = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Space not found: " + id));
        space.setStatus(parseStatus(status));
        return ParkingSpaceResponse.from(repo.save(space));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new NotFoundException("Space not found: " + id);
        repo.deleteById(id);
    }

    private ParkingSpace.SpaceStatus parseStatus(String s) {
        if (s == null) return ParkingSpace.SpaceStatus.AVAILABLE;
        return ParkingSpace.SpaceStatus.valueOf(s.toUpperCase());
    }
}
