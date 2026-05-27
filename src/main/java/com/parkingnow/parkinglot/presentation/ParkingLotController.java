package com.parkingnow.parkinglot.presentation;

import com.parkingnow.parkinglot.application.ParkingLotService;
import com.parkingnow.parkinglot.application.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parking-lots")
@RequiredArgsConstructor
public class ParkingLotController {

    private final ParkingLotService service;

    @GetMapping
    public List<ParkingLotResponse> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ParkingLotResponse getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<ParkingLotResponse> create(@Valid @RequestBody CreateParkingLotRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }

    @PutMapping("/{id}")
    public ParkingLotResponse update(@PathVariable Long id, @RequestBody CreateParkingLotRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
