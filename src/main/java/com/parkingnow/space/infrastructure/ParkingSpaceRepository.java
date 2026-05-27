package com.parkingnow.space.infrastructure;

import com.parkingnow.space.domain.ParkingSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ParkingSpaceRepository extends JpaRepository<ParkingSpace, Long> {
    List<ParkingSpace> findByLotId(Long lotId);
    Optional<ParkingSpace> findByCodeAndLotId(String code, Long lotId);
    boolean existsByCodeAndLotId(String code, Long lotId);

    @Query("SELECT COUNT(s) FROM ParkingSpace s WHERE s.lotId = :lotId AND LOWER(s.status) = LOWER(:status)")
    int countByLotIdAndStatus(Long lotId, String status);
}
