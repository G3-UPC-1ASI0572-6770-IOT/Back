package com.parkingnow.reservation.application;

import com.parkingnow.parkinglot.domain.ParkingLot;
import com.parkingnow.parkinglot.infrastructure.ParkingLotRepository;
import com.parkingnow.reservation.application.dto.*;
import com.parkingnow.reservation.domain.Reservation;
import com.parkingnow.reservation.infrastructure.ReservationRepository;
import com.parkingnow.shared.exception.ConflictException;
import com.parkingnow.shared.exception.NotFoundException;
import com.parkingnow.space.application.ParkingSpaceService;
import com.parkingnow.space.domain.ParkingSpace;
import com.parkingnow.space.infrastructure.ParkingSpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository repo;
    private final ParkingSpaceRepository spaceRepo;
    private final ParkingLotRepository lotRepo;
    private final ParkingSpaceService spaceService;

    @Transactional
    public ReservationResponse create(ReservationRequest req, Long driverId, String driverEmail) {
        ParkingSpace space = spaceRepo.findById(req.getSpaceId())
                .orElseThrow(() -> new NotFoundException("Space not found: " + req.getSpaceId()));

        if (space.getConsolidatedStatus() != ParkingSpace.ConsolidatedStatus.AVAILABLE) {
            throw new ConflictException("Space is not FREE. Current status: " + space.getConsolidatedStatus());
        }

        if (repo.countActiveBySpaceId(req.getSpaceId()) > 0) {
            throw new ConflictException("Space already has an active reservation");
        }

        ParkingLot lot = lotRepo.findById(space.getLotId())
                .orElseThrow(() -> new NotFoundException("Parking lot not found"));

        Reservation r = Reservation.builder()
                .spaceId(space.getId())
                .spaceLabel(space.getCode())
                .lotId(lot.getId())
                .parkingLotName(lot.getName())
                .driverId(driverId)
                .driverEmail(driverEmail)
                .status(Reservation.ReservationStatus.ACTIVE)
                .expiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
                .build();
        r = repo.save(r);

        space.setLogicalStatus(ParkingSpace.LogicalStatus.RESERVED);
        spaceService.consolidateStatus(space);
        spaceRepo.save(space);

        return ReservationResponse.from(r);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findByDriver(Long driverId) {
        return repo.findByDriverId(driverId).stream()
                .map(ReservationResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findByLot(Long lotId) {
        return repo.findByLotId(lotId).stream()
                .map(ReservationResponse::from).toList();
    }

    @Transactional
    public ReservationResponse cancel(Long id, Long driverId) {
        Reservation r = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation not found: " + id));
        if (driverId != null && !driverId.equals(r.getDriverId())) {
            throw new ConflictException("Not your reservation");
        }
        if (r.getStatus() != Reservation.ReservationStatus.ACTIVE) {
            throw new ConflictException("Only ACTIVE reservations can be cancelled");
        }
        r.setStatus(Reservation.ReservationStatus.CANCELLED);
        r.setCancelledAt(Instant.now());

        freeSpace(r.getSpaceId());
        return ReservationResponse.from(repo.save(r));
    }

    @Transactional
    public ReservationResponse consume(Long id) {
        Reservation r = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation not found: " + id));
        if (r.getStatus() != Reservation.ReservationStatus.ACTIVE) {
            throw new ConflictException("Only ACTIVE reservations can be consumed");
        }
        r.setStatus(Reservation.ReservationStatus.CONSUMED);
        r.setConsumedAt(Instant.now());

        spaceRepo.findById(r.getSpaceId()).ifPresent(space -> {
            space.setPhysicalStatus(ParkingSpace.PhysicalStatus.OCCUPIED);
            spaceService.consolidateStatus(space);
            spaceRepo.save(space);
        });

        return ReservationResponse.from(repo.save(r));
    }

    @Transactional
    public void expireStale() {
        Instant now = Instant.now();
        repo.findExpired(now).forEach(r -> {
            r.setStatus(Reservation.ReservationStatus.EXPIRED);
            repo.save(r);
            freeSpace(r.getSpaceId());
        });
    }

    private void freeSpace(Long spaceId) {
        if (spaceId == null) return;
        spaceRepo.findById(spaceId).ifPresent(space -> {
            space.setLogicalStatus(ParkingSpace.LogicalStatus.AVAILABLE);
            spaceService.consolidateStatus(space);
            spaceRepo.save(space);
        });
    }
}
