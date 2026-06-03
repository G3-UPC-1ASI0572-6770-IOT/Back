package com.parkingnow.iot.infrastructure;

import com.parkingnow.iot.domain.IotEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface IotEventRepository extends JpaRepository<IotEvent, Long> {

    List<IotEvent> findAllByOrderByReceivedAtDesc(Pageable pageable);

    List<IotEvent> findByNodeIdOrderByReceivedAtDesc(String nodeId, Pageable pageable);

    List<IotEvent> findByParkingSpaceIdOrderByReceivedAtDesc(Long parkingSpaceId, Pageable pageable);

    List<IotEvent> findByDetectedStatus(IotEvent.DetectedStatus status, Pageable pageable);

    long countByReceivedAtAfter(Instant since);
}
