package com.parkingnow.event.infrastructure;

import com.parkingnow.event.domain.EventAlert;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventAlertRepository extends JpaRepository<EventAlert, Long> {
    List<EventAlert> findByLotId(Long lotId);
    List<EventAlert> findBySeverity(EventAlert.Severity severity);
    List<EventAlert> findAllByOrderByCreatedAtDesc(Pageable pageable);
    int countBySeverity(EventAlert.Severity severity);
}
