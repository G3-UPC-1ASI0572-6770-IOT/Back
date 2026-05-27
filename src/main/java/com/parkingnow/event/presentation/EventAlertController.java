package com.parkingnow.event.presentation;

import com.parkingnow.event.application.EventAlertService;
import com.parkingnow.event.application.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventAlertController {

    private final EventAlertService service;

    @GetMapping
    public List<EventAlertResponse> getAll(@RequestParam(defaultValue = "100") int limit) {
        return service.findAll(limit);
    }

    @GetMapping("/{id}")
    public EventAlertResponse getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<EventAlertResponse> create(@Valid @RequestBody EventAlertRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }
}
