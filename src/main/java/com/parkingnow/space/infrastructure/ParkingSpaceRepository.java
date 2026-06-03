package com.parkingnow.space.infrastructure;

import com.parkingnow.space.domain.ParkingSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParkingSpaceRepository extends JpaRepository<ParkingSpace, Long> {
    List<ParkingSpace> findByLotId(Long lotId);
    Optional<ParkingSpace> findByCodeAndLotId(String code, Long lotId);
    boolean existsByCodeAndLotId(String code, Long lotId);

    Optional<ParkingSpace> findByIotNodeId(String iotNodeId);

    Optional<ParkingSpace> findByLotIdAndCode(Long lotId, String code);

    @Query("SELECT COUNT(s) FROM ParkingSpace s WHERE s.lotId = :lotId AND s.consolidatedStatus = :status")
    int countByLotIdAndStatus(@Param("lotId") Long lotId,
                              @Param("status") ParkingSpace.ConsolidatedStatus status);
}
