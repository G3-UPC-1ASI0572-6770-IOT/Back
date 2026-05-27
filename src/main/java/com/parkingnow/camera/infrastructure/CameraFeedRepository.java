package com.parkingnow.camera.infrastructure;

import com.parkingnow.camera.domain.CameraFeed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CameraFeedRepository extends JpaRepository<CameraFeed, Long> {
    List<CameraFeed> findByParkingLotId(Long parkingLotId);
    Optional<CameraFeed> findFirstByParkingLotIdOrderByLastSeenAtDesc(Long parkingLotId);
}
