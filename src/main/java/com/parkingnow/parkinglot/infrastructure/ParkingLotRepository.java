package com.parkingnow.parkinglot.infrastructure;

import com.parkingnow.parkinglot.domain.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {
    List<ParkingLot> findByOwnerId(Long ownerId);
    Optional<ParkingLot> findFirstByOwnerId(Long ownerId);
}
