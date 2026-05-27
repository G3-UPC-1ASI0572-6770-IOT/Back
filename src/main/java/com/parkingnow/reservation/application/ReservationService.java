package com.parkingnow.reservation.application;

import com.parkingnow.reservation.application.dto.*;
import com.parkingnow.reservation.domain.Reservation;
import com.parkingnow.reservation.infrastructure.ReservationRepository;
import com.parkingnow.shared.exception.NotFoundException;
import com.parkingnow.space.application.ParkingSpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository repo;
    private final ParkingSpaceService spaceService;

    public List<ReservationResponse> findAll() {
        return repo.findAll().stream().map(ReservationResponse::from).toList();
    }

    public List<ReservationResponse> findByLot(Long lotId) {
        return repo.findByLotId(lotId).stream().map(ReservationResponse::from).toList();
    }

    public ReservationResponse findById(Long id) {
        return repo.findById(id).map(ReservationResponse::from)
                .orElseThrow(() -> new NotFoundException("Reservation not found: " + id));
    }

    @Transactional
    public ReservationResponse create(ReservationRequest req) {
        String code = "RS-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Reservation r = Reservation.builder()
                .code(code)
                .driverName(req.getDriverName())
                .driverPhone(req.getDriverPhone())
                .spaceId(req.getSpaceId())
                .lotId(req.getLotId())
                .startTime(req.getStartTime() != null ? LocalTime.parse(req.getStartTime()) : LocalTime.now())
                .endTime(req.getEndTime() != null ? LocalTime.parse(req.getEndTime()) : null)
                .status(Reservation.ReservationStatus.ACTIVE)
                .build();
        r = repo.save(r);
        // Mark space as reserved
        spaceService.updateStatus(req.getSpaceId(), "reserved");
        return ReservationResponse.from(r);
    }

    @Transactional
    public ReservationResponse cancel(Long id) {
        Reservation r = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation not found: " + id));
        r.setStatus(Reservation.ReservationStatus.CANCELLED);
        // Free the space
        spaceService.updateStatus(r.getSpaceId(), "available");
        return ReservationResponse.from(repo.save(r));
    }

    @Transactional
    public ReservationResponse finish(Long id) {
        Reservation r = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation not found: " + id));
        r.setStatus(Reservation.ReservationStatus.FINISHED);
        spaceService.updateStatus(r.getSpaceId(), "available");
        return ReservationResponse.from(repo.save(r));
    }

    @Transactional
    public ReservationResponse consume(Long id) {
        Reservation r = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation not found: " + id));
        r.setStatus(Reservation.ReservationStatus.CONSUMED);
        spaceService.updateStatus(r.getSpaceId(), "occupied");
        return ReservationResponse.from(repo.save(r));
    }
}
