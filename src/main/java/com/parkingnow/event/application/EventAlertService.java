package com.parkingnow.event.application;

import com.parkingnow.event.application.dto.*;
import com.parkingnow.event.domain.EventAlert;
import com.parkingnow.event.infrastructure.EventAlertRepository;
import com.parkingnow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventAlertService {

    private final EventAlertRepository repo;

    public List<EventAlertResponse> findAll(int limit) {
        return repo.findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit))
                .stream().map(EventAlertResponse::from).toList();
    }

    public List<EventAlertResponse> findByLot(Long lotId) {
        return repo.findByLotId(lotId).stream().map(EventAlertResponse::from).toList();
    }

    public EventAlertResponse findById(Long id) {
        return repo.findById(id).map(EventAlertResponse::from)
                .orElseThrow(() -> new NotFoundException("Event not found: " + id));
    }

    @Transactional
    public EventAlertResponse create(EventAlertRequest req) {
        EventAlert event = EventAlert.builder()
                .severity(EventAlert.Severity.valueOf(req.getSeverity().toUpperCase()))
                .title(req.getTitle())
                .message(req.getMessage())
                .lotId(req.getLotId())
                .spaceId(req.getSpaceId())
                .nodeId(req.getNodeId())
                .build();
        return EventAlertResponse.from(repo.save(event));
    }
}
