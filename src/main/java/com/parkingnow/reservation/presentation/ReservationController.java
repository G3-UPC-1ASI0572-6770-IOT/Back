package com.parkingnow.reservation.presentation;

import com.parkingnow.reservation.application.ReservationService;
import com.parkingnow.reservation.application.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService service;

    @GetMapping
    public List<ReservationResponse> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ReservationResponse getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> create(@Valid @RequestBody ReservationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }

    @PatchMapping("/{id}/cancel")
    public ReservationResponse cancel(@PathVariable Long id) {
        return service.cancel(id);
    }

    @PatchMapping("/{id}/finish")
    public ReservationResponse finish(@PathVariable Long id) {
        return service.finish(id);
    }

    @PatchMapping("/{id}/consume")
    public ReservationResponse consume(@PathVariable Long id) {
        return service.consume(id);
    }

    @GetMapping("/lot/{lotId}")
    public List<ReservationResponse> getByLot(@PathVariable Long lotId) {
        return service.findByLot(lotId);
    }
}
