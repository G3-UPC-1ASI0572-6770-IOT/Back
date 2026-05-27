package com.parkingnow.parkinglot.application;

import com.parkingnow.parkinglot.application.dto.*;
import com.parkingnow.parkinglot.domain.ParkingLot;
import com.parkingnow.parkinglot.infrastructure.ParkingLotRepository;
import com.parkingnow.shared.exception.NotFoundException;
import com.parkingnow.space.infrastructure.ParkingSpaceRepository;
import com.parkingnow.iot.infrastructure.IotNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
                .hourlyRate(req.getHourlyRate() != null ? req.getHourlyRate() : java.math.BigDecimal.valueOf(3.50))
                .lotType(req.getLotType() != null ? req.getLotType() : "open")
                .ownerId(req.getOwnerId())
                .build();
        lot = lotRepository.save(lot);
        return ParkingLotResponse.from(lot, 0, 0);
    }

    public List<ParkingLotResponse> findAll() {
        return lotRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ParkingLotResponse> findByOwner(Long ownerId) {
        return lotRepository.findByOwnerId(ownerId).stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<ParkingLotResponse> findByOwnerId(Long ownerId) {
        return lotRepository.findFirstByOwnerId(ownerId).map(this::toResponse);
    }

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
        return toResponse(lotRepository.save(lot));
    }

    @Transactional
    public void delete(Long id) {
        if (!lotRepository.existsById(id)) throw new NotFoundException("Parking lot not found: " + id);
        lotRepository.deleteById(id);
    }

    private ParkingLotResponse toResponse(ParkingLot lot) {
        int occupied = spaceRepository.countByLotIdAndStatus(lot.getId(), "occupied");
        int iotNodes = iotNodeRepository.countByLotId(lot.getId());
        return ParkingLotResponse.from(lot, occupied, iotNodes);
    }
}
